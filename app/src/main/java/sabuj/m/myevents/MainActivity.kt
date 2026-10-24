package sabuj.m.myevents

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.feature.lock.LockEvent
import sabuj.m.myevents.feature.lock.LockViewModel
import sabuj.m.myevents.feature.lock.PinScreen
import sabuj.m.myevents.navigation.AppNavHost

/**
 * Single Activity. Flow:
 *  1. System splash screen (~500 ms via SplashScreen API)
 *  2. No PIN set   → PIN setup screen
 *  3. PIN set      → PIN unlock screen (biometric prompt on fingerprint tap, PIN fallback)
 *  4. Authenticated → NavHost (drawer + all screens)
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val lockViewModel: LockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { false }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // isDark follows system setting; Phase 8 will allow manual override via Settings
            val isDark = isSystemInDarkTheme()
            AppTheme(isDark = isDark) {
                val state by lockViewModel.state.collectAsState()

                when {
                    state.isUnlocked -> {
                        AppNavHost(modifier = Modifier.fillMaxSize())
                    }
                    !lockViewModel.isPinSetup -> {
                        // First launch — two-step PIN creation
                        PinScreen(
                            title    = if (state.isConfirming) "Confirm your PIN" else "Create your PIN",
                            subtitle = if (state.isConfirming)
                                "Enter the same PIN again"
                            else
                                "4 digits to protect your data",
                            pinLength       = state.pin.length,
                            hasError        = state.error != null,
                            errorMessage    = state.error,
                            showFingerprint = false,
                            onDigit         = { lockViewModel.onEvent(LockEvent.DigitPressed(it)) },
                            onBackspace     = { lockViewModel.onEvent(LockEvent.BackspacePressed) },
                        )
                    }
                    else -> {
                        // Unlock screen
                        PinScreen(
                            title    = "Enter PIN",
                            subtitle = "Use your PIN or fingerprint to unlock",
                            pinLength        = state.pin.length,
                            hasError         = state.error != null,
                            errorMessage     = state.error,
                            isLockedOut      = state.isLockedOut,
                            lockedOutMessage = "Too many attempts. Try again in ${state.lockoutSecondsLeft}s.",
                            showFingerprint  = lockViewModel.isBiometricEnabled && isBiometricAvailable(),
                            onDigit          = { lockViewModel.onEvent(LockEvent.DigitPressed(it)) },
                            onBackspace      = { lockViewModel.onEvent(LockEvent.BackspacePressed) },
                            onFingerprint    = { showBiometricPrompt() },
                        )
                    }
                }
            }
        }
    }

    /** Returns true if the device has at least one enrolled biometric credential. */
    private fun isBiometricAvailable(): Boolean =
        BiometricManager.from(this)
            .canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS

    /** Launches the system BiometricPrompt dialog. */
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    lockViewModel.onEvent(LockEvent.BiometricSuccess)
                }
                override fun onAuthenticationFailed() {
                    lockViewModel.onEvent(LockEvent.BiometricFailed)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // User cancelled or hardware unavailable — stay on PIN screen silently
                }
            })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock MyEvents")
            .setSubtitle("Use your fingerprint to continue")
            .setNegativeButtonText("Use PIN")
            .build()
        prompt.authenticate(info)
    }
}