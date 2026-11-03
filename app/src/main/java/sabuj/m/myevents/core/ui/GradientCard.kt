package sabuj.m.myevents.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.LocalAppColors
import sabuj.m.myevents.core.theme.PaletteId
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.gradientFor

/**
 * Card with a gradient background — used for featured/hero cards on the Home screen.
 * [slot] (0–5) controls which hue from the palette gradient table is used so adjacent
 * cards look different.
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    slot: Int = 0,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalAppColors.current
    val brush = gradientFor(slot = slot.coerceIn(0, 5), palette = PaletteId.A, isDark = colors.isDark)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.card))
            .background(brush),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg), content = content)
    }
}

@LightDarkPreviews
@Composable
private fun GradientCardPreview() {
    AppTheme {
        GradientCard(modifier = Modifier.padding(Spacing.lg)) {
            Text("Gradient card", style = bodyM, color = AppColor.onPrimary)
        }
    }
}
