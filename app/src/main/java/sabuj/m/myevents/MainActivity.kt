package sabuj.m.myevents

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.ui.doodles.HeroIllustration
import sabuj.m.myevents.feature.lock.LockEvent
import sabuj.m.myevents.feature.lock.LockViewModel
import sabuj.m.myevents.feature.lock.PinScreen
import sabuj.m.myevents.navigation.AppNavHost
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG as BS
import androidx.compose.runtime.Composable

/**
 * Single Activity. Flow:
 *  1. System splash screen (~500 ms via SplashScreen API)
 *  2. No PIN set   → PIN setup screen
 *  3. PIN set      → PIN unlock screen (biometric prompt on fingerprint tap, PIN fallback)
 *  4. Authenticated → NavHost (drawer + all screens)
 *
 * Re-lock rule (S1-05): if the app is in the background for more than
 * [RE_LOCK_THRESHOLD_MS] (60 s), the unlock state is cleared on resume.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val lockViewModel: LockViewModel by viewModels()

    @Inject lateinit var pinStore: sabuj.m.myevents.data.security.PinStore
    @Inject lateinit var settingsDataStore: sabuj.m.myevents.data.settings.SettingsDataStore

    companion object {
        /** 60 seconds — if the app is backgrounded longer than this, re-lock. */
        private const val RE_LOCK_THRESHOLD_MS = 60_000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by settingsDataStore.userSettingsFlow.collectAsState(
                initial = sabuj.m.myevents.data.settings.UserSettings()
            )
            val isDark = when (settings.themeMode) {
                sabuj.m.myevents.data.settings.ThemeMode.Light  -> false
                sabuj.m.myevents.data.settings.ThemeMode.Dark   -> true
                sabuj.m.myevents.data.settings.ThemeMode.System -> isSystemInDarkTheme()
            }

            // A1 & A2: Splash duration increased by 100% (1.2s), persisted via rememberSaveable
            // so theme/configuration changes never re-trigger a glimpse of the splash screen.
            var showSplash by rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                if (showSplash) {
                    delay(1200.milliseconds)
                    showSplash = false
                }
            }

            AppTheme(palette = settings.paletteId, isDark = isDark) {
                val state by lockViewModel.state.collectAsState()

                if (showSplash) {
                    CustomSplashScreen()
                } else {
                    when {
                        state.isUnlocked || state.authStep == sabuj.m.myevents.feature.lock.AuthStep.UNLOCKED -> {
                            AppNavHost(modifier = Modifier.fillMaxSize())
                        }
                        state.authStep == sabuj.m.myevents.feature.lock.AuthStep.NEED_SIGN_IN -> {
                            PinScreen(
                                title             = "Sign In",
                                subtitle          = "Sign in with your email or Google account to continue",
                                pinLength         = 0,
                                needSignIn        = true,
                                errorMessage      = state.error,
                                onSignInWithEmail = { email, pass -> lockViewModel.signInWithEmail(email, pass) },
                                onSignInWithGoogle = { launchGoogleSignIn() },
                            )
                        }
                        state.authStep == sabuj.m.myevents.feature.lock.AuthStep.NEED_PIN_SETUP -> {
                            PinScreen(
                                title             = if (state.isConfirming) "Confirm your PIN" else "Create your PIN",
                                subtitle          = if (state.isConfirming) "Enter the same PIN again" else "4 digits to protect your data",
                                pinLength         = state.pin.length,
                                userEmail         = state.userEmail,
                                userName          = state.userName,
                                hasError          = state.error != null,
                                errorMessage      = state.error,
                                showFingerprint   = false,
                                onDigit           = { lockViewModel.onEvent(LockEvent.DigitPressed(it)) },
                                onBackspace       = { lockViewModel.onEvent(LockEvent.BackspacePressed) },
                            )
                        }
                        else -> {
                            // NEED_PIN_UNLOCK
                            val bioEnabled = pinStore.isBiometricEnabled && settings.fingerprintEnabled
                            androidx.compose.runtime.LaunchedEffect(bioEnabled) {
                                if (bioEnabled && isBiometricAvailable()) {
                                    showBiometricPrompt()
                                }
                            }

                            PinScreen(
                                title            = "Enter PIN",
                                subtitle         = if (bioEnabled) "Use your PIN or fingerprint to unlock" else "Enter your 4-digit PIN to unlock",
                                pinLength        = state.pin.length,
                                userEmail        = state.userEmail,
                                userName         = state.userName,
                                hasError         = state.error != null,
                                errorMessage     = state.error,
                                isLockedOut      = state.isLockedOut,
                                lockedOutMessage = "Too many attempts. Try again in ${state.lockoutSecondsLeft}s.",
                                showFingerprint  = bioEnabled && isBiometricAvailable(),
                                onDigit          = { lockViewModel.onEvent(LockEvent.DigitPressed(it)) },
                                onBackspace      = { lockViewModel.onEvent(LockEvent.BackspacePressed) },
                                onFingerprint    = { if (bioEnabled && isBiometricAvailable()) showBiometricPrompt() },
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun CustomSplashScreen() {
        ScreenScaffold {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    HeroIllustration(size = 120.dp)
                    Spacer(Modifier.height(Spacing.md))
                    androidx.compose.material3.Text(
                        text = "My Events",
                        style = titleL,
                        color = AppColor.text,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    androidx.compose.material3.Text(
                        text = "Made with ❤️ by Sabuj",
                        style = bodyM,
                        color = AppColor.primary,
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = Spacing.xl),
                ) {
                    androidx.compose.material3.Text(
                        text = "Version ${BuildConfig.APP_VERSION}",
                        style = labelM,
                        color = AppColor.text3,
                    )
                }
            }
        }
    }

    /** Returns true if the device has at least one enrolled biometric credential. */
    private fun isBiometricAvailable(): Boolean =
        BiometricManager.from(this)
            .canAuthenticate(BS) == BiometricManager.BIOMETRIC_SUCCESS

    private fun launchGoogleSignIn() {
        androidx.lifecycle.lifecycleScope.launch {
            try {
                val credentialManager = androidx.credentials.CredentialManager.create(this@MainActivity)

                val resId = resources.getIdentifier("default_web_client_id", "string", packageName)
                val webClientId = if (resId != 0) getString(resId) else "10902216485-dummy.apps.googleusercontent.com"

                val googleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = androidx.credentials.GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context = this@MainActivity, request = request)
                val credential = result.credential

                if (credential is androidx.credentials.CustomCredential &&
                    credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = com.google.firebase.auth.GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    lockViewModel.signInWithCredential(firebaseCredential)
                }
            } catch (e: Exception) {
                android.util.Log.e("GoogleSignIn", "Google Sign-In failed: ${e.message}", e)
            }
        }
    }

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
                    // User canceled or hardware unavailable — stay on PIN screen silently
                }
            })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock MyEvents")
            .setSubtitle("Use your fingerprint to continue")
            .setAllowedAuthenticators(BS)
            .setNegativeButtonText("Use PIN")
            .build()
        prompt.authenticate(info)
    }

    // -----------------------------------------------------------------------
    // Re-lock after 60 s in background (S1-05)
    // -----------------------------------------------------------------------

    /** Record the time when the app goes to background. */
    override fun onPause() {
        super.onPause()
        pinStore.saveBackgroundTime(System.currentTimeMillis())
    }

    /**
     * On resume, check if the app was in the background longer than
     * [RE_LOCK_THRESHOLD_MS]. If so, dispatch [LockEvent.ReLock].
     */
    override fun onResume() {
        super.onResume()
        val bgTime = pinStore.getBackgroundTime()
        if (bgTime > 0 && System.currentTimeMillis() - bgTime > RE_LOCK_THRESHOLD_MS) {
            lockViewModel.onEvent(LockEvent.ReLock)
        }
        // Clear the background timestamp so a quick resume doesn't re-lock
        pinStore.saveBackgroundTime(-1L)
    }
}

@Composable
fun CustomSplashScreen() {
    ScreenScaffold {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                HeroIllustration(size = 120.dp)
                Spacer(Modifier.height(Spacing.md))
                androidx.compose.material3.Text(
                    text = "My Events",
                    style = titleL,
                    color = AppColor.text,
                )
                Spacer(Modifier.height(Spacing.xs))
                androidx.compose.material3.Text(
                    text = "Made with ❤️ by Sabuj",
                    style = bodyM,
                    color = AppColor.primary,
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = Spacing.xl),
            ) {
                androidx.compose.material3.Text(
                    text = "Version ${BuildConfig.APP_VERSION}",
                    style = labelM,
                    color = AppColor.text3,
                )
            }
        }
    }
}

@LightDarkPreviews
@Composable
private fun CustomSplashScreenPreview() {
    AppTheme {
        CustomSplashScreen()
    }
}