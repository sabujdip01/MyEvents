package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing

/**
 * Floating three-line menu button shown at the top-left of every screen.
 * Design.md section 4.1:
 *  - 48 × 48 circle, `surface`, soft shadow (light) or 1 dp outline (dark)
 *  - Icon `ic-menu` 24 dp in `primary`
 *  - Floats at left 16 dp, top = status bar + 8 dp
 *
 * @param onClick Called when the button is tapped (opens the drawer).
 */
@Composable
fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick      = onClick,
        modifier     = modifier.size(48.dp),
        shape        = CircleShape,
        color        = AppColor.surface,
        tonalElevation = if (AppColor.isDark) 0.dp else 3.dp,
        shadowElevation = if (AppColor.isDark) 0.dp else 4.dp,
        border       = if (AppColor.isDark)
            androidx.compose.foundation.BorderStroke(1.dp, AppColor.outlineVariant)
        else null,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector        = AppIcon.Menu.fallback,
                contentDescription = "Open menu",
                tint               = AppColor.primary,
                modifier           = Modifier.size(24.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun MenuButtonPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
        ) {
            MenuButton(onClick = {})
        }
    }
}
