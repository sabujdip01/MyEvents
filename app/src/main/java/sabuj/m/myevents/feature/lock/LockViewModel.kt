package sabuj.m.myevents.feature.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.data.remote.FirebaseAuthManager
import sabuj.m.myevents.data.remote.FirestoreSyncRepository
import sabuj.m.myevents.data.security.PinHasher
import sabuj.m.myevents.data.security.PinStore
import javax.inject.Inject

/** Max wrong attempts before lockout. Decided: 3 (see GEMINI.md Phase 1 note). */
private const val MAX_ATTEMPTS  = 3
/** Lockout duration in seconds. */
private const val LOCKOUT_SECS  = 30L

enum class AuthStep {
    NEED_SIGN_IN,
    CHECKING_FIREBASE,
    NEED_PIN_SETUP,
    NEED_PIN_UNLOCK,
    UNLOCKED,
}

/**
 * Drives authentication, PIN setup, and unlock flows.
 */
@HiltViewModel
class LockViewModel @Inject constructor(
    private val pinStore : PinStore,
    private val pinHasher: PinHasher,
    private val authManager: FirebaseAuthManager,
    private val syncRepo: FirestoreSyncRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LockUiState())
    val state: StateFlow<LockUiState> = _state.asStateFlow()

    init {
        checkPersistedLockout()
        checkAuthAndPinState()
    }

    private fun checkPersistedLockout() {
        val lockoutUntil = pinStore.lockoutUntilEpochMs
        val now = System.currentTimeMillis()
        if (lockoutUntil > now) {
            val secsLeft = ((lockoutUntil - now) / 1000L).toInt().coerceAtLeast(1)
            startLockout(secsLeft)
        } else {
            _state.update { it.copy(failedAttempts = pinStore.failedAttempts) }
        }
    }

    fun checkAuthAndPinState() {
        viewModelScope.launch {
            val user = authManager.currentUser
            if (user == null || user.isAnonymous) {
                _state.update { it.copy(authStep = AuthStep.NEED_SIGN_IN, isLoading = false) }
            } else {
                val name = user.displayName?.ifBlank { null }
                    ?: user.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
                _state.update {
                    it.copy(
                        userEmail = user.email,
                        userName = name,
                        isLoading = true,
                    )
                }
                checkRemotePin(user.uid)
            }
        }
    }

    private suspend fun checkRemotePin(uid: String) {
        try {
            syncRepo.syncRemotePin(uid)
            if (pinStore.isPinSet) {
                _state.update { it.copy(authStep = AuthStep.NEED_PIN_UNLOCK, isLoading = false) }
                syncRepo.performSync()
            } else {
                _state.update { it.copy(authStep = AuthStep.NEED_PIN_SETUP, isLoading = false) }
            }
        } catch (e: Exception) {
            if (pinStore.isPinSet) {
                _state.update { it.copy(authStep = AuthStep.NEED_PIN_UNLOCK, isLoading = false) }
            } else {
                _state.update { it.copy(authStep = AuthStep.NEED_PIN_SETUP, isLoading = false) }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Derived helpers read once at init
    // -----------------------------------------------------------------------

    /** True → show setup; False → show unlock. */
    val isPinSetup: Boolean get() = pinStore.isPinSet

    val isBiometricEnabled: Boolean get() = pinStore.isBiometricEnabled

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(error = "Signing in...", isLoading = true) }
            val res = authManager.signInWithEmail(email, pass)
            if (res.isSuccess) {
                _state.update { it.copy(error = null, isLoading = false) }
                checkAuthAndPinState()
            } else {
                val err = res.exceptionOrNull()?.localizedMessage ?: "Sign in failed"
                _state.update { it.copy(error = err, isLoading = false) }
            }
        }
    }

    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _state.update { it.copy(error = "Signing in with Google...", isLoading = true) }
            val res = authManager.signInWithCredential(credential)
            if (res.isSuccess) {
                _state.update { it.copy(error = null, isLoading = false) }
                checkAuthAndPinState()
            } else {
                val err = res.exceptionOrNull()?.localizedMessage ?: "Google sign in failed"
                _state.update { it.copy(error = err, isLoading = false) }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Events
    // -----------------------------------------------------------------------

    fun onEvent(event: LockEvent) {
        when (event) {
            is LockEvent.DigitPressed  -> handleDigit(event.digit)
            is LockEvent.BackspacePressed -> handleBackspace()
            is LockEvent.BiometricSuccess -> onUnlockSuccess()
            is LockEvent.BiometricFailed  -> { /* PIN pad stays visible */ }
            is LockEvent.ReLock           -> reLock()
        }
    }

    // -----------------------------------------------------------------------
    // PIN input handling
    // -----------------------------------------------------------------------

    private fun handleDigit(digit: Int) {
        val s = _state.value
        if (s.isLockedOut || s.pin.length >= 4) return

        val newPin = s.pin + digit.toString()
        _state.update { it.copy(pin = newPin, error = null) }

        if (newPin.length == 4) {
            if (pinStore.isPinSet) verifyPin(newPin) else handleSetupPin(newPin)
        }
    }

    private fun handleBackspace() {
        _state.update { it.copy(pin = it.pin.dropLast(1), error = null) }
    }

    // -----------------------------------------------------------------------
    // Setup flow (two-step: enter then confirm)
    // -----------------------------------------------------------------------

    private fun handleSetupPin(pin: String) {
        val s = _state.value
        if (s.setupFirstPin == null) {
            // First entry — save and ask to confirm
            _state.update { it.copy(setupFirstPin = pin, pin = "", isConfirming = true) }
        } else {
            // Confirmation entry
            if (pin == s.setupFirstPin) {
                val hash = pinHasher.hash(pin)
                pinStore.savePin(hash)
                pinStore.failedAttempts = 0
                pinStore.lockoutUntilEpochMs = 0L
                _state.update { it.copy(isUnlocked = true, authStep = AuthStep.UNLOCKED) }

                // Sync PIN hash & pull/push data to Firebase
                viewModelScope.launch {
                    runCatching {
                        val uid = authManager.ensureSignedIn()
                        syncRepo.pushPinToRemote(uid, hash)
                        syncRepo.performSync()
                    }
                }
            } else {
                // Mismatch — shake and restart
                _state.update { it.copy(pin = "", setupFirstPin = null, isConfirming = false, error = "PINs do not match. Try again.") }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Unlock flow
    // -----------------------------------------------------------------------

    private fun verifyPin(pin: String) {
        val hash = pinStore.getHash() ?: return
        if (pinHasher.verify(pin, hash)) {
            pinStore.failedAttempts = 0
            pinStore.lockoutUntilEpochMs = 0L
            onUnlockSuccess()
        } else {
            val newAttempts = pinStore.failedAttempts + 1
            pinStore.failedAttempts = newAttempts
            if (newAttempts >= MAX_ATTEMPTS) {
                val lockoutUntil = System.currentTimeMillis() + (LOCKOUT_SECS * 1000L)
                pinStore.lockoutUntilEpochMs = lockoutUntil
                startLockout(LOCKOUT_SECS.toInt())
            } else {
                val remaining = MAX_ATTEMPTS - newAttempts
                _state.update {
                    it.copy(
                        pin           = "",
                        failedAttempts = newAttempts,
                        error         = "Incorrect PIN. $remaining attempt${if (remaining == 1) "" else "s"} remaining.",
                    )
                }
            }
        }
    }

    private fun onUnlockSuccess() {
        _state.update { it.copy(isUnlocked = true, authStep = AuthStep.UNLOCKED, failedAttempts = 0, error = null) }
        viewModelScope.launch {
            runCatching {
                syncRepo.performSync()
            }
        }
    }

    /**
     * Re-locks the app when [LockEvent.ReLock] is received.
     */
    private fun reLock() {
        if (pinStore.isPinSet) {
            val now = System.currentTimeMillis()
            val lockoutUntil = pinStore.lockoutUntilEpochMs
            val isLockedOut = lockoutUntil > now
            val secsLeft = if (isLockedOut) ((lockoutUntil - now) / 1000L).toInt().coerceAtLeast(1) else 0

            _state.update {
                it.copy(
                    isUnlocked         = false,
                    authStep           = AuthStep.NEED_PIN_UNLOCK,
                    pin                = "",
                    error              = null,
                    failedAttempts     = pinStore.failedAttempts,
                    isLockedOut        = isLockedOut,
                    lockoutSecondsLeft = secsLeft,
                )
            }

            if (isLockedOut) {
                startLockout(secsLeft)
            }
        }
    }

    private fun startLockout(seconds: Int = LOCKOUT_SECS.toInt()) {
        _state.update { it.copy(pin = "", isLockedOut = true, lockoutSecondsLeft = seconds, error = null) }
        viewModelScope.launch {
            repeat(seconds) {
                delay(1_000)
                _state.update {
                    val next = (it.lockoutSecondsLeft - 1).coerceAtLeast(0)
                    it.copy(lockoutSecondsLeft = next)
                }
            }
            pinStore.failedAttempts = 0
            pinStore.lockoutUntilEpochMs = 0L
            _state.update { it.copy(isLockedOut = false, failedAttempts = 0) }
        }
    }
}

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class LockUiState(
    val authStep: AuthStep       = AuthStep.NEED_SIGN_IN,
    val userEmail: String?       = null,
    val userName: String?        = null,
    val pin: String              = "",
    val setupFirstPin: String?   = null,
    val isConfirming: Boolean    = false,
    val isUnlocked: Boolean      = false,
    val error: String?           = null,
    val isLoading: Boolean       = false,
    val failedAttempts: Int      = 0,
    val isLockedOut: Boolean     = false,
    val lockoutSecondsLeft: Int  = 0,
)

// ---------------------------------------------------------------------------
// Events
// ---------------------------------------------------------------------------

sealed interface LockEvent {
    data class DigitPressed(val digit: Int) : LockEvent
    data object BackspacePressed : LockEvent
    data object BiometricSuccess : LockEvent
    data object BiometricFailed  : LockEvent
    /** Fired by MainActivity when app resumes after > 60 s in background. */
    data object ReLock           : LockEvent
}
