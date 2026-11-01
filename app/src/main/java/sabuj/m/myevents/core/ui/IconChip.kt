package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.icons.AppIcon

/**
 * Icon chip component — design.md section 4.3.
 *
 * Sizes:
 *  - Default (`.icon-chip`): 44 × 44, radius 14, icon 24 dp
 *  - Sm (`.icon-chip--sm`):  36 × 36, radius 12, icon 20 dp
 *  - Lg (`.icon-chip--lg`):  64 × 64, radius 20, icon 32 dp
 *
 * On gradient cards, pass [containerColor] and [iconColor] from [gradientChipColor]
 * and [gradientIconColor] instead of the default primary-container pair.
 */
@Composable
fun IconChip(
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: IconChipSize = IconChipSize.Default,
    containerColor: Color = AppColor.primaryContainer,
    iconColor: Color = AppColor.primary,
) {
    Surface(
        modifier = modifier.size(size.boxSize),
        shape    = RoundedCornerShape(size.cornerRadius),
        color    = containerColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector        = icon,
                contentDescription = contentDescription,
                tint               = iconColor,
                modifier           = Modifier.size(size.iconSize),
            )
        }
    }
}

/** Status-coloured variants — danger / success / warning. */
@Composable
fun IconChipDanger(
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: IconChipSize = IconChipSize.Default,
) = IconChip(icon, contentDescription, modifier, size, AppColor.dangerContainer, AppColor.danger)

@Composable
fun IconChipSuccess(
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: IconChipSize = IconChipSize.Default,
) = IconChip(icon, contentDescription, modifier, size, AppColor.successContainer, AppColor.success)

@Composable
fun IconChipWarning(
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: IconChipSize = IconChipSize.Default,
) = IconChip(icon, contentDescription, modifier, size, AppColor.warningContainer, AppColor.warning)

/** Size variants for [IconChip]. */
enum class IconChipSize(val boxSize: Dp, val cornerRadius: Dp, val iconSize: Dp) {
    Sm      (36.dp, 12.dp, 20.dp),
    Default (44.dp, Radius.icon, 24.dp),
    Lg      (64.dp, 20.dp, 32.dp),
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun IconChipPreview() {
    AppTheme {
        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
            IconChip(
                icon = AppIcon.Events.fallback,
                contentDescription = "",
                size = IconChipSize.Default,
            )
        }
    }
}
