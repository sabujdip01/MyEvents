package sabuj.m.myevents.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Full set of colour tokens the app uses. Screens never use raw hex;
 * they read from [LocalAppColors].current.
 *
 * Token names match design.md section 3.1 exactly so diffs are easy.
 */
@Immutable
data class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val accent: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val text: Color,
    val text2: Color,
    val text3: Color,
    val doodle: Color,
    // Fixed status colours — same in every palette (design.md 3.3)
    val danger: Color,
    val dangerContainer: Color,
    val onDangerContainer: Color,
    val success: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val scrim: Color,
    val isDark: Boolean,
)

/** CompositionLocal that provides [AppColors] down the tree. */
val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No AppColors provided — wrap your UI in AppTheme")
}

// ---------------------------------------------------------------------------
// Fixed status colours (identical in every palette, design.md 3.3)
// ---------------------------------------------------------------------------

internal val StatusLight = object {
    val danger           = Color(0xFFDC2626)
    val dangerContainer  = Color(0xFFFEE2E2)
    val onDangerContainer= Color(0xFF7F1D1D)
    val success          = Color(0xFF16A34A)
    val successContainer = Color(0xFFDCFCE7)
    val onSuccessContainer= Color(0xFF14532D)
    val warning          = Color(0xFFD97706)
    val warningContainer = Color(0xFFFEF3C7)
    val onWarningContainer= Color(0xFF78350F)
}

internal val StatusDark = object {
    val danger           = Color(0xFFF87171)
    val dangerContainer  = Color(0xFF3F1214)
    val onDangerContainer= Color(0xFFFECACA)
    val success          = Color(0xFF4ADE80)
    val successContainer = Color(0xFF0F3A22)
    val onSuccessContainer= Color(0xFFBBF7D0)
    val warning          = Color(0xFFFBBF24)
    val warningContainer = Color(0xFF3D2A06)
    val onWarningContainer= Color(0xFFFDE68A)
}
