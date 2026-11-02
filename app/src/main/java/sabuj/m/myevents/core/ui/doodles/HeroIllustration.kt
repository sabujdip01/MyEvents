package sabuj.m.myevents.core.ui.doodles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

/**
 * Desk-calendar hero illustration matching Sample UI (Home-Light.png) 1:1.
 * Features a 3D tilted desk calendar with binder rings, center heart, floating hearts, and leafy sprigs.
 *
 * @param size Diameter of bounding box. Use 120 dp on Home, 180 dp in drawer.
 */
@Composable
fun HeroIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
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
    // Ground shadow
    drawOval(
        color   = doodle.copy(alpha = 0.15f),
        topLeft = Offset(s * 0.15f, s * 0.82f),
        size    = Size(s * 0.70f, s * 0.12f),
    )

    // Floating hearts behind (top-right & top-left matching Sample UI)
    drawHeart(s * 0.82f, s * 0.22f, s * 0.12f, primary.copy(alpha = 0.75f))
    drawHeart(s * 0.66f, s * 0.12f, s * 0.16f, primary.copy(alpha = 0.85f))
    drawHeart(s * 0.92f, s * 0.38f, s * 0.08f, primary.copy(alpha = 0.50f))

    // Leaf sprigs fanning out
    drawLeafSprig(s * 0.18f, s * 0.75f, -25f, accent, s)
    drawLeafSprig(s * 0.82f, s * 0.75f, 25f, accent, s)

    // Slight 3D stance rotation (-8 degrees)
    rotate(degrees = -8f, pivot = Offset(s * 0.5f, s * 0.5f)) {
        val calLeft   = s * 0.22f
        val calTop    = s * 0.22f
        val calRight  = s * 0.78f
        val calBottom = s * 0.82f
        val calW      = calRight - calLeft
        val calH      = calBottom - calTop
        val stripH    = calH * 0.26f
        val cornerR   = s * 0.08f

        // Calendar body
        val bodyPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left   = calLeft,   top    = calTop,
                    right  = calRight,  bottom = calBottom,
                    radiusX = cornerR,  radiusY = cornerR,
                )
            )
        }
        drawPath(bodyPath, color = if (isDark) primaryContainer else surface, style = Fill)

        // Subtle 3D right depth edge
        val edgeW = s * 0.025f
        drawRect(
            color   = primary.copy(alpha = 0.40f),
            topLeft = Offset(calRight - edgeW, calTop + cornerR),
            size    = Size(edgeW, calH - cornerR),
        )

        // Top binder strip (primary color)
        val stripPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(calLeft, calTop, calRight, calTop + stripH),
                    topLeft = CornerRadius(cornerR),
                    topRight = CornerRadius(cornerR),
                )
            )
        }
        drawPath(stripPath, color = primary, style = Fill)

        // Three 3D binder rings
        val ringY  = calTop - s * 0.02f
        val ringR  = s * 0.038f
        val ringXs = listOf(calLeft + calW * 0.25f, calLeft + calW * 0.50f, calLeft + calW * 0.75f)
        for (rx in ringXs) {
            drawCircle(color = primary, radius = ringR, center = Offset(rx, ringY), style = Stroke(width = s * 0.028f))
            drawCircle(color = surface, radius = ringR * 0.35f, center = Offset(rx - ringR * 0.3f, ringY - ringR * 0.3f))
        }

        // Center 3D heart on page
        val heartCX = calLeft + calW * 0.5f
        val heartCY = calTop + stripH + (calH - stripH) * 0.48f
        val heartSize = calW * 0.40f
        drawHeart(heartCX, heartCY, heartSize, primary)
        drawHeart(heartCX - heartSize * 0.10f, heartCY - heartSize * 0.10f, heartSize * 0.25f, surface.copy(alpha = 0.40f))
    }
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

@LightDarkPreviews
@Composable
private fun HeroIllustrationPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            HeroIllustration(size = 180.dp)
        }
    }
}
