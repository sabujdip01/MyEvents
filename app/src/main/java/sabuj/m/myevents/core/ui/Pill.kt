package sabuj.m.myevents.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.labelM

/**
 * Pill label — design.md section 4.4.
 * Height 28, horizontal padding 10, r-pill, [primaryContainer] fill by default.
 * Optional leading [icon] at 16 dp.
 *
 * Variants: Default, Success, Warning, Danger — pass [variant] or custom colours.
 */
@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    variant: PillVariant = PillVariant.Default,
) {
    val colors = variant.colors()
    Surface(
        modifier = modifier.height(28.dp),
        shape    = RoundedCornerShape(Radius.pill),
        color    = colors.container,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = colors.content,
                    modifier           = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text  = text,
                style = labelM,
                color = colors.content,
            )
        }
    }
}

enum class PillVariant {
    Default, Success, Warning, Danger, Muted;

    @Composable
    fun colors(): PillColors = when (this) {
        Default -> PillColors(AppColor.primaryContainer, AppColor.onPrimaryContainer)
        Success -> PillColors(AppColor.successContainer, AppColor.onSuccessContainer)
        Warning -> PillColors(AppColor.warningContainer, AppColor.onWarningContainer)
        Danger  -> PillColors(AppColor.dangerContainer,  AppColor.onDangerContainer)
        Muted   -> PillColors(AppColor.surfaceVariant,    AppColor.text2)
    }
}

data class PillColors(val container: Color, val content: Color)

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun PillPreview() {
    AppTheme {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Pill("2 events")
            Spacer(Modifier.width(8.dp))
            Pill("12 days left", variant = PillVariant.Warning)
            Spacer(Modifier.width(8.dp))
            Pill("Today", variant = PillVariant.Danger)
        }
    }
}
