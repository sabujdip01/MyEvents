package sabuj.m.myevents.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Root theme composable. Wrap every screen (and every preview) in this.
 *
 * @param palette  One of the four palettes (A–D). Default A.
 * @param isDark   Whether to use the dark variant. Default false.
 * @param content  The UI content.
 */
@Composable
fun AppTheme(
    palette: PaletteId = PaletteId.A,
    isDark: Boolean = false,
    content: @Composable () -> Unit,
) {
    val appColors = paletteColors(palette, isDark)
    val m3Scheme  = appColors.toM3ColorScheme()

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            typography  = AppTypography,
            shapes      = AppShapes,
            content     = content,
        )
    }
}

/** Convenience accessor — shorter than [LocalAppColors].current */
val AppColor: AppColors
    @Composable get() = LocalAppColors.current

// ---------------------------------------------------------------------------
// Map AppColors → Material 3 ColorScheme so M3 components inherit our tokens
// ---------------------------------------------------------------------------
private fun AppColors.toM3ColorScheme(): ColorScheme =
    if (!isDark) lightColorScheme(
        primary            = primary,
        onPrimary          = onPrimary,
        primaryContainer   = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary          = accent,
        onSecondary        = onPrimary,
        secondaryContainer = primaryContainer,
        onSecondaryContainer = onPrimaryContainer,
        background         = background,
        onBackground       = text,
        surface            = surface,
        onSurface          = text,
        surfaceVariant     = surfaceVariant,
        onSurfaceVariant   = text2,
        outline            = outline,
        outlineVariant     = outlineVariant,
        error              = danger,
        onError            = onPrimary,
        errorContainer     = dangerContainer,
        onErrorContainer   = onDangerContainer,
        scrim              = scrim,
    )
    else darkColorScheme(
        primary            = primary,
        onPrimary          = onPrimary,
        primaryContainer   = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary          = accent,
        onSecondary        = onPrimary,
        secondaryContainer = primaryContainer,
        onSecondaryContainer = onPrimaryContainer,
        background         = background,
        onBackground       = text,
        surface            = surface,
        onSurface          = text,
        surfaceVariant     = surfaceVariant,
        onSurfaceVariant   = text2,
        outline            = outline,
        outlineVariant     = outlineVariant,
        error              = danger,
        onError            = onPrimary,
        errorContainer     = dangerContainer,
        onErrorContainer   = onDangerContainer,
        scrim              = scrim,
    )
