package sabuj.m.myevents.feature.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import sabuj.m.myevents.R
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.display
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.numberL
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.AppButton
import sabuj.m.myevents.core.ui.AppField
import sabuj.m.myevents.core.ui.doodles.BgBlobs
import kotlin.math.roundToInt

/**
 * Shared PIN & Mandatory Sign-In screen composable.
 */
@Composable
fun PinScreen(
    title: String,
    subtitle: String,
    pinLength: Int,
    modifier: Modifier = Modifier,
    needSignIn: Boolean = false,
    userEmail: String? = null,
    userName: String? = null,
    hasError: Boolean = false,
    errorMessage: String? = null,
    isLockedOut: Boolean = false,
    lockedOutMessage: String = "",
    showFingerprint: Boolean = false,
    onDigit: (Int) -> Unit = {},
    onBackspace: () -> Unit = {},
    onFingerprint: () -> Unit = {},
    onSignInWithEmail: (String, String) -> Unit = { _, _ -> },
    onSignInWithGoogle: () -> Unit = {},
) {
    // Shake animation for error
    val shakeKey = remember(hasError) { if (hasError) System.currentTimeMillis() else 0L }
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(shakeKey) {
        if (shakeKey != 0L) {
            repeat(4) {
                shakeOffset.animateTo( 10f, tween(50))
                shakeOffset.animateTo(-10f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColor.background),
    ) {
        BgBlobs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.fillMaxSize(0.10f))

            if (needSignIn) {
                // ── MANDATORY SIGN-IN LAYOUT ──
                Text(
                    text      = "Sign In",
                    style     = display,
                    color     = AppColor.text,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text      = "Sign in with Email & Password or Google account",
                    style     = bodyM,
                    color     = AppColor.text2,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Spacing.lg))

                var emailInput by remember { mutableStateOf("") }
                var passwordInput by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    AppField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = "Email Address",
                        placeholder = "you@example.com",
                    )
                    AppField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = "Password",
                        placeholder = "••••••••",
                    )
                    Spacer(Modifier.height(Spacing.xs))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            style = bodyM,
                            color = AppColor.danger,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(Spacing.xs))
                    }

                    AppButton(
                        text = "Sign In",
                        onClick = {
                            if (emailInput.isNotBlank() && passwordInput.isNotBlank()) {
                                onSignInWithEmail(emailInput.trim(), passwordInput)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // Continue with Google Button featuring official Google Logo icon
                    Surface(
                        onClick = onSignInWithGoogle,
                        shape = RoundedCornerShape(Radius.card),
                        color = AppColor.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(22.dp),
                            )
                            Spacer(Modifier.width(Spacing.md))
                            Text(
                                text = "Continue with Google",
                                style = titleS,
                                color = AppColor.text,
                            )
                        }
                    }
                }
            } else {
                // ── PIN SETUP / UNLOCK LAYOUT ──
                Text(
                    text      = title,
                    style     = display,
                    color     = AppColor.text,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Spacing.xs))

                Text(
                    text      = subtitle,
                    style     = bodyM,
                    color     = AppColor.text2,
                    textAlign = TextAlign.Center,
                )

                if (!userEmail.isNullOrEmpty()) {
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text      = "Account: ${userName ?: ""} ($userEmail)",
                        style     = labelM,
                        color     = AppColor.primary,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(Spacing.xl2))

                // PIN dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                ) {
                    repeat(4) { index ->
                        val filled = index < pinLength
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .then(
                                    if (filled) Modifier.background(
                                        if (hasError) AppColor.danger else AppColor.primary
                                    )
                                    else Modifier.border(
                                        2.dp,
                                        if (hasError) AppColor.danger else AppColor.outline,
                                        CircleShape,
                                    )
                                ),
                        )
                    }
                }

                // Error / lockout message
                Spacer(Modifier.height(Spacing.md))
                val msg = when {
                    isLockedOut    -> lockedOutMessage
                    errorMessage != null -> errorMessage
                    else           -> ""
                }
                Text(
                    text  = msg,
                    style = bodyM,
                    color = if (isLockedOut) AppColor.warning else AppColor.danger,
                    textAlign = TextAlign.Center,
                    minLines = 1,
                )

                Spacer(Modifier.weight(1f))

                // Keypad
                PinKeypad(
                    enabled        = !isLockedOut,
                    showFingerprint = showFingerprint,
                    onDigit        = onDigit,
                    onBackspace    = onBackspace,
                    onFingerprint  = onFingerprint,
                    modifier       = Modifier.padding(bottom = Spacing.xl3),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Keypad (3 × 4 grid)
// ---------------------------------------------------------------------------

@Composable
private fun PinKeypad(
    enabled: Boolean,
    showFingerprint: Boolean,
    onDigit: (Int) -> Unit,
    onBackspace: () -> Unit,
    onFingerprint: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9),
    )
    Column(
        modifier              = modifier,
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.spacedBy(Spacing.md),
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl3)) {
                row.forEach { digit ->
                    PinKey(
                        label   = digit.toString(),
                        enabled = enabled,
                        onClick = { onDigit(digit) },
                    )
                }
            }
        }
        // Bottom row: fingerprint | 0 | backspace
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xl3)) {
            PinKey(
                icon    = if (showFingerprint) Icons.Rounded.Fingerprint else null,
                label   = "",
                enabled = enabled && showFingerprint,
                onClick = onFingerprint,
                iconTint = AppColor.primary,
            )
            PinKey(label = "0", enabled = enabled, onClick = { onDigit(0) })
            PinKey(
                icon    = Icons.Rounded.Backspace,
                label   = "",
                enabled = enabled,
                onClick = onBackspace,
                iconTint = AppColor.text2,
            )
        }
    }
}

@Composable
private fun PinKey(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: Color = AppColor.text,
) {
    Surface(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.size(72.dp),
        shape    = CircleShape,
        color    = if (enabled) AppColor.surfaceVariant else AppColor.surfaceVariant.copy(alpha = 0.5f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (icon != null) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(28.dp))
            } else if (label.isNotEmpty()) {
                Text(
                    text  = label,
                    style = numberL,
                    color = AppColor.text,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun PinScreenSetupPreview() {
    AppTheme {
        PinScreen(
            title    = "Create your PIN",
            subtitle = "4 digits to protect your data",
            pinLength = 2,
        )
    }
}

@LightDarkPreviews
@Composable
private fun PinScreenErrorPreview() {
    AppTheme {
        PinScreen(
            title        = "Enter PIN",
            subtitle     = "Use your PIN or fingerprint to unlock",
            pinLength    = 4,
            hasError     = true,
            errorMessage = "Incorrect PIN. 2 attempts remaining.",
            showFingerprint = true,
        )
    }
}

@LightDarkPreviews
@Composable
private fun PinScreenLockedPreview() {
    AppTheme {
        PinScreen(
            title            = "Enter PIN",
            subtitle         = "Use your PIN or fingerprint to unlock",
            pinLength        = 0,
            isLockedOut      = true,
            lockedOutMessage = "Too many attempts. Try again in 28 seconds.",
            showFingerprint  = true,
        )
    }
}
