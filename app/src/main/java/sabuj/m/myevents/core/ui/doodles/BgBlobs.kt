package sabuj.m.myevents.core.ui.doodles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor

/**
 * Two soft oversized circles drawn behind the top-right of every screen.
 * Sizes and overlap follow design.md section 3.5:
 *  - Circle 1: 240 dp, top-right, partly off-screen
 *  - Circle 2: 150 dp, 60 dp left and 70 dp lower than circle 1
 * Fill: [AppColor].doodle at 8% (light) or 14% (dark).
 */
@Composable
fun BgBlobs(modifier: Modifier = Modifier) {
    val doodle = AppColor.doodle
    val alpha  = if (AppColor.isDark) 0.14f else 0.08f
    val color  = doodle.copy(alpha = alpha)

    Canvas(modifier = modifier.fillMaxSize()) {
        val r1 = 120.dp.toPx()   // 240 dp diameter → 120 dp radius
        val r2 =  75.dp.toPx()   // 150 dp diameter →  75 dp radius
        val c1 = Offset(size.width + r1 * 0.35f, -r1 * 0.25f)
        val c2 = Offset(c1.x - 60.dp.toPx(), c1.y + 70.dp.toPx())
        drawCircle(color = color, radius = r1, center = c1)
        drawCircle(color = color, radius = r2, center = c2)
    }
}
