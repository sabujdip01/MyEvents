package sabuj.m.myevents.core.ui.doodles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor

/**
 * Desk-calendar hero illustration drawn entirely with Compose Canvas.
 * Described in design.md section 5.8.
 *
 * @param size  Diameter of the bounding box. Use 128 dp on Home, 180 dp in drawer.
 */
@Composable
fun HeroIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 128.dp,
) {
    val primary          = AppColor.primary
    val surface          = AppColor.surface
    val primaryContainer = AppColor.primaryContainer
    val accent           = AppColor.accent
    val doodle           = AppColor.doodle
    val isDark           = AppColor.isDark

    Canvas(modifier = modifier.size(size)) {
        val s = this.size.width  // treat as square
        drawCalendar(s, primary, surface, primaryContainer, accent, doodle, isDark)
    }
}

private fun DrawScope.drawCalendar(
    s: Float,
    primary: Color,
    surface: Color,
    primaryContainer: Color,
    accent: Color,
    doodle: Color,
    isDark: Boolean,
) {
    val calLeft   = s * 0.10f
    val calTop    = s * 0.18f
    val calRight  = s * 0.88f
    val calBottom = s * 0.88f
    val calW      = calRight - calLeft
    val calH      = calBottom - calTop
    val stripH    = calH * 0.24f
    val cornerR   = s * 0.10f

    // Ground shadow
    drawOval(
        color      = doodle.copy(alpha = 0.12f),
        topLeft    = Offset(calLeft + calW * 0.1f, calBottom - s * 0.03f),
        size       = androidx.compose.ui.geometry.Size(calW * 0.8f, s * 0.06f),
    )

    // Left leaf sprig (bottom-left)
    drawLeafSprig(
        pivotX  = calLeft + calW * 0.05f,
        pivotY  = calBottom - s * 0.02f,
        angleDeg = -30f,
        color   = accent.copy(alpha = 0.70f),
        s       = s,
    )

    // Right leaf sprig (bottom-right)
    drawLeafSprig(
        pivotX  = calRight - calW * 0.05f,
        pivotY  = calBottom - s * 0.02f,
        angleDeg = 30f,
        color   = accent.copy(alpha = 0.70f),
        s       = s,
    )

    // Calendar body (white/surface rounded rectangle)
    val bodyPath = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left   = calLeft,   top    = calTop,
                right  = calRight,  bottom = calBottom,
                radiusX = cornerR,  radiusY = cornerR,
            )
        )
    }
    drawPath(bodyPath, color = if (isDark) primaryContainer else surface, style = Fill)

    // Top strip (primary, same rounded top, flat bottom)
    val stripPath = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left   = calLeft,          top    = calTop,
                right  = calRight,         bottom = calTop + stripH,
                topLeftRadius  = androidx.compose.ui.geometry.CornerRadius(cornerR),
                topRightRadius = androidx.compose.ui.geometry.CornerRadius(cornerR),
                bottomLeftRadius  = androidx.compose.ui.geometry.CornerRadius(0f),
                bottomRightRadius = androidx.compose.ui.geometry.CornerRadius(0f),
            )
        )
    }
    val stripColor = if (isDark) primary.copy(alpha = 0.85f) else primary
    drawPath(stripPath, color = stripColor, style = Fill)

    // Three binder rings poking above the strip
    val ringY    = calTop - s * 0.03f
    val ringR    = s * 0.038f
    val ringXs   = listOf(calLeft + calW * 0.25f, calLeft + calW * 0.50f, calLeft + calW * 0.75f)
    for (rx in ringXs) {
        drawCircle(color = primary, radius = ringR, center = Offset(rx, ringY), style = Stroke(width = s * 0.028f))
        // Highlight on left edge
        drawCircle(color = surface.copy(alpha = 0.5f), radius = ringR * 0.4f, center = Offset(rx - ringR * 0.4f, ringY - ringR * 0.3f))
    }

    // Heart in the page centre
    val heartCX = calLeft + calW * 0.5f
    val heartCY = calTop + stripH + (calH - stripH) * 0.50f
    val heartSize = calW * 0.35f
    drawHeart(heartCX, heartCY, heartSize, primary)
    // Heart highlight
    drawHeart(heartCX - heartSize * 0.12f, heartCY - heartSize * 0.12f, heartSize * 0.28f, surface.copy(alpha = 0.45f))

    // Right side depth edge
    val edgeW = s * 0.025f
    drawRect(
        color   = primary.copy(alpha = 0.55f),
        topLeft = Offset(calRight - edgeW, calTop + cornerR),
        size    = androidx.compose.ui.geometry.Size(edgeW, calH - cornerR),
    )

    // Floating small hearts
    drawCircleHeart(calLeft + calW * 0.70f, calTop - s * 0.10f, s * 0.055f, primary.copy(alpha = 0.60f))
    drawCircleHeart(calLeft + calW * 0.90f, calTop - s * 0.18f, s * 0.038f, primary.copy(alpha = 0.35f))
}

/** Draws a heart centred at (cx, cy) with the given [size] (height) and [color]. */
private fun DrawScope.drawHeart(cx: Float, cy: Float, size: Float, color: Color) {
    val path = Path().apply {
        val r = size / 2f
        moveTo(cx, cy + r * 0.8f)
        cubicTo(cx - r * 1.4f, cy, cx - r * 1.4f, cy - r * 1.2f, cx, cy - r * 0.4f)
        cubicTo(cx + r * 1.4f, cy - r * 1.2f, cx + r * 1.4f, cy, cx, cy + r * 0.8f)
        close()
    }
    drawPath(path, color = color, style = Fill)
}

/** A small filled circle used as a tiny heart placeholder at small sizes. */
private fun DrawScope.drawCircleHeart(cx: Float, cy: Float, r: Float, color: Color) {
    drawHeart(cx, cy, r * 2f, color)
}

/** Five slim leaf shapes fanning out from [pivotX], [pivotY] at [angleDeg]. */
private fun DrawScope.drawLeafSprig(
    pivotX: Float, pivotY: Float,
    angleDeg: Float,
    color: Color,
    s: Float,
) {
    val leafW = s * 0.045f
    val leafH = s * 0.13f
    rotate(degrees = angleDeg, pivot = Offset(pivotX, pivotY)) {
        for (i in -2..2) {
            val offsetX = i * leafW * 0.9f
            val top     = pivotY - leafH * (0.6f + kotlin.math.abs(i) * 0.1f)
            val path = Path().apply {
                moveTo(pivotX + offsetX, pivotY)
                cubicTo(
                    pivotX + offsetX - leafW, top + leafH * 0.5f,
                    pivotX + offsetX - leafW * 0.3f, top,
                    pivotX + offsetX, top + leafH * 0.15f,
                )
                cubicTo(
                    pivotX + offsetX + leafW * 0.3f, top,
                    pivotX + offsetX + leafW, top + leafH * 0.5f,
                    pivotX + offsetX, pivotY,
                )
                close()
            }
            drawPath(path, color = color, style = Fill)
        }
    }
}
