package sabuj.m.myevents.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

/**
 * Floating menu button shown at the top-left of every screen.
 * Design.md section 4.1:
 *  - 48 × 48 circle, surface colour, soft shadow (light) or 1 dp outline (dark)
 *  - Two thick horizontal lines (≡ simplified), drawn in primary colour
 *  - Floats at left 16 dp, top = status bar + 8 dp
 *
 * @param onClick Called when the button is tapped (opens the drawer).
 */
@Composable
fun MenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = AppColor.primary

    Surface(
        onClick         = onClick,
        modifier        = modifier.size(48.dp),
        shape           = CircleShape,
        color           = androidx.compose.ui.graphics.Color.Transparent,
        shadowElevation = 2.dp,
        border          = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else AppColor.primary.copy(alpha = 0.25f),
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
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
