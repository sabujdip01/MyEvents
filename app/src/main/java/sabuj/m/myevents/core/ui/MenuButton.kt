package sabuj.m.myevents.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

/**
 * Floating menu button shown at the top-left of every screen (Items C1 & C2).
 * Perfect anti-aliased circle with transparent background fill and thin border.
 */
@Composable
fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = AppColor.primary
    val borderColor = if (AppColor.isDark) AppColor.outlineVariant else AppColor.primary.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .drawBehind {
                drawCircle(
                    color = borderColor,
                    style = Stroke(width = 1.dp.toPx()),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        // Two centered lines — 15 dp wide (25% shorter), 3.45 dp stroke (15% thicker)
        Canvas(modifier = Modifier.size(15.dp, 10.dp)) {
            val w      = size.width
            val stroke = 3.45.dp.toPx()
            val gap    = 6.dp.toPx()
            val midY   = size.height / 2f
            val topY   = midY - (gap / 2f)
            val botY   = midY + (gap / 2f)

            listOf(topY, botY).forEach { y ->
                drawLine(
                    color       = lineColor,
                    start       = Offset(0f, y),
                    end         = Offset(w, y),
                    strokeWidth = stroke,
                    cap         = StrokeCap.Round,
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
