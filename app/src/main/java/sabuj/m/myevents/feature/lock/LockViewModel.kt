package sabuj.m.myevents.feature.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.data.security.PinHasher
import sabuj.m.myevents.data.security.PinStore
import javax.inject.Inject

/** Max wrong attempts before lockout. Decided: 3 (see GEMINI.md Phase 1 note). */
private const val MAX_ATTEMPTS  = 3
/** Lockout duration in seconds. */
private const val LOCKOUT_SECS  = 30L

/**
 * Drives both PIN setup and unlock flows.
 * The UI observes [state] and sends [onEvent].
 */
@HiltViewModel
class LockViewModel @Inject constructor(
    private val pinStore : PinStore,
    private val pinHasher: PinHasher,
) : ViewModel() {

    private val _state = MutableStateFlow(LockUiState())
    val state: StateFlow<LockUiState> = _state.asStateFlow()

    // -----------------------------------------------------------------------
    // Derived helpers read once at init
    // -----------------------------------------------------------------------

    /** True → show setup; False → show unlock. */
    val isPinSetup: Boolean get() = pinStore.isPinSet

    val isBiometricEnabled: Boolean get() = pinStore.isBiometricEnabled

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
                pinStore.savePin(pinHasher.hash(pin))
                _state.update { it.copy(isUnlocked = true) }
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
            onUnlockSuccess()
        } else {
            val newAttempts = _state.value.failedAttempts + 1
            if (newAttempts >= MAX_ATTEMPTS) {
                startLockout()
            } else {
                _state.update {
                    it.copy(
                        pin           = "",
                        failedAttempts = newAttempts,
                        error         = "Incorrect PIN. ${MAX_ATTEMPTS - newAttempts} attempt${if (MAX_ATTEMPTS - newAttempts == 1) "" else "s"} remaining.",
                    )
                }
            }
        }
    }

    private fun onUnlockSuccess() {
        _state.update { it.copy(isUnlocked = true, failedAttempts = 0, error = null) }
    }

    /**
     * Re-locks the app when [LockEvent.ReLock] is received.
     * Only acts if a PIN is actually set (no PIN → no lock).
     */
    private fun reLock() {
        if (pinStore.isPinSet) {
            _state.update {
                it.copy(
                    isUnlocked     = false,
                    pin            = "",
                    error          = null,
                    failedAttempts = 0,
                    isLockedOut    = false,
                )
            }
        }
    }

    private fun startLockout() {
        _state.update { it.copy(pin = "", isLockedOut = true, lockoutSecondsLeft = LOCKOUT_SECS.toInt(), error = null) }
        viewModelScope.launch {
            repeat(LOCKOUT_SECS.toInt()) {
                delay(1_000)
                _state.update { it.copy(lockoutSecondsLeft = it.lockoutSecondsLeft - 1) }
            }
            _state.update { it.copy(isLockedOut = false, failedAttempts = 0) }
        }
    }
}

// ---------------------------------------------------------------------------
// UI state
// ---------------------------------------------------------------------------

data class LockUiState(
    val pin: String              = "",
    val setupFirstPin: String?   = null,
    val isConfirming: Boolean    = false,
    val isUnlocked: Boolean      = false,
    val error: String?           = null,
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
