package sabuj.m.myevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.feature.lock.LockEvent
import sabuj.m.myevents.feature.lock.LockUiState
import sabuj.m.myevents.feature.lock.LockViewModel
import sabuj.m.myevents.feature.lock.PinScreen
import sabuj.m.myevents.navigation.AppNavHost

/**
 * Single Activity. Flow:
 *  1. System splash screen (~500 ms via SplashScreen API)
 *  2. No PIN set   → PIN setup screen
 *  3. PIN set      → PIN unlock screen (fingerprint prompt first)
 *  4. Authenticated → NavHost (drawer + all screens)
 *
 * Back-from-home relocks after 60 s background (TODO Phase 1 follow-up).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val lockViewModel: LockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen and hold it until ViewModel is ready
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { false } // 500 ms default; extend if needed

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // TODO Phase 8: read palette + isDark from DataStore/Settings
            AppTheme {
                val state by lockViewModel.state.collectAsState()

                when {
                    state.isUnlocked -> {
                        // Authenticated — show the app
                        AppNavHost(modifier = Modifier.fillMaxSize())
                    }
                    !lockViewModel.isPinSetup -> {
                        // First launch — PIN setup (two-step)
                        PinScreen(
                            title    = if (state.isConfirming) "Confirm your PIN" else "Create your PIN",
                            subtitle = if (state.isConfirming)
                                "Enter the same PIN again"
                            else
                                "4 digits to protect your data",
                            pinLength      = state.pin.length,
                            hasError       = state.error != null,
                            errorMessage   = state.error,
                            showFingerprint = false,
                            onDigit        = { lockViewModel.onEvent(LockEvent.DigitPressed(it)) },
                            onBackspace    = { lockViewModel.onEvent(LockEvent.BackspacePressed) },
                        )
                    }
                    else -> {
                        // Unlock
                        PinScreen(
                            title    = "Enter PIN",
                            subtitle = "Use your PIN or fingerprint to unlock",
                            pinLength       = state.pin.length,
                            hasError        = state.error != null,
                            errorMessage    = state.error,
                            isLockedOut     = state.isLockedOut,
                            lockedOutMessage = "Too many attempts. Try again in ${state.lockoutSecondsLeft}s.",
                            showFingerprint = lockViewModel.isBiometricEnabled,
                            onDigit         = { lockViewModel.onEvent(LockEvent.DigitPressed(it)) },
                            onBackspace     = { lockViewModel.onEvent(LockEvent.BackspacePressed) },
                            onFingerprint   = { /* TODO Phase 1b: BiometricPrompt */ },
                        )
                    }
                }
            }
        }
    }
}