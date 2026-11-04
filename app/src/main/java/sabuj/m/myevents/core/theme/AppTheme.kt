package sabuj.m.myevents.core.theme

import android.view.ContextThemeWrapper
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import sabuj.m.myevents.R

/** Returns the XML style resource for [palette] and [isDark] mode. */
fun getPaletteThemeResId(palette: PaletteId, isDark: Boolean): Int = when (palette) {
    PaletteId.A -> if (!isDark) R.style.Theme_MyEvents_PaletteA_Light else R.style.Theme_MyEvents_PaletteA_Dark
    PaletteId.B -> if (!isDark) R.style.Theme_MyEvents_PaletteB_Light else R.style.Theme_MyEvents_PaletteB_Dark
    PaletteId.C -> if (!isDark) R.style.Theme_MyEvents_PaletteC_Light else R.style.Theme_MyEvents_PaletteC_Dark
    PaletteId.D -> if (!isDark) R.style.Theme_MyEvents_PaletteD_Light else R.style.Theme_MyEvents_PaletteD_Dark
}

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
    isDark: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColors = paletteColors(palette, isDark)
    val m3Scheme  = appColors.toM3ColorScheme()

    val context = LocalContext.current
    val themedContext = remember(palette, isDark) {
        val themeResId = getPaletteThemeResId(palette, isDark)
        ContextThemeWrapper(context, themeResId)
    }

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalContext provides themedContext,
    ) {
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
