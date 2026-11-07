package sabuj.m.myevents.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Spacing

/**
 * Universal Floating Action Button for MyEvents (Items F1 & F2).
 * Round shape with thin border matching [MenuButton] and clean surface fill.
 * Includes [navigationBarsPadding] so FAB never overlaps or overflows into system navigation bar.
 */
@Composable
fun AppFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        onClick         = { if (enabled) onClick() },
        modifier        = modifier
            .navigationBarsPadding()
            .padding(bottom = Spacing.lg, end = Spacing.lg)
            .size(56.dp),
        shape           = CircleShape,
        color           = if (enabled) AppColor.surface else AppColor.surfaceVariant.copy(alpha = 0.5f),
        shadowElevation = if (enabled) 2.dp else 0.dp,
        border          = BorderStroke(
            0.8.dp,
            if (enabled) {
                if (AppColor.isDark) AppColor.outlineVariant else AppColor.primary.copy(alpha = 0.25f)
            } else AppColor.outlineVariant.copy(alpha = 0.3f),
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector        = icon,
                contentDescription = contentDescription,
                tint               = if (enabled) AppColor.primary else AppColor.text3,
                modifier           = Modifier.size(24.dp),
            )
        }
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun AppFabPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        AppFab(
            onClick            = {},
            icon               = androidx.compose.material.icons.Icons.Rounded.Add,
            contentDescription = "Add",
        )
    }
}
