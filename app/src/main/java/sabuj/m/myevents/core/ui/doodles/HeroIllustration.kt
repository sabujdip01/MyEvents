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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
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
 * Desk-calendar hero illustration matching design.md §5.8 and Sample UI Nav-Light.png.
 *
 * A table calendar viewed from the front-left with a purple header strip,
 * three binder rings, a plump heart on the white page, leafy sprigs on both
 * sides, floating hearts above, and a ground shadow. Background blobs are
 * drawn outside this composable (by the screen / drawer).
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
        val s = this.size.width // square
        drawCalendarScene(s, primary, surface, primaryContainer, accent, doodle, isDark)
    }
}

// ---------------------------------------------------------------------------
// Scene composition
// ---------------------------------------------------------------------------

private fun DrawScope.drawCalendarScene(
    s: Float,
    primary: Color,
    surface: Color,
    primaryContainer: Color,
    accent: Color,
    doodle: Color,
    isDark: Boolean,
) {
    val pivot = Offset(s * 0.50f, s * 0.50f)

    // ── Background blobs (design.md §6.1 drawer: 200 dp & 120 dp) ──────────
    drawCircle(
        color  = doodle.copy(alpha = if (isDark) 0.14f else 0.08f),
        radius = s * 0.55f,
        center = Offset(s * 0.38f, s * 0.78f),
    )
    drawCircle(
        color  = doodle.copy(alpha = if (isDark) 0.10f else 0.06f),
        radius = s * 0.33f,
        center = Offset(s * 0.20f, s * 0.50f),
    )

    // ── Everything tilted ~-8° (front-left viewpoint) ───────────────────────
    rotate(degrees = -8f, pivot = pivot) {

        // Calendar bounds
        val calL = s * 0.20f
        val calT = s * 0.22f
        val calR = s * 0.78f
        val calB = s * 0.84f
        val calW = calR - calL
        val calH = calB - calT
        val cr   = s * 0.06f         // corner radius
        val stripH = calH * 0.26f    // purple header height

        // ── Leaf sprigs (behind calendar, accent@70%) ───────────────────────
        drawLeafSprig(
            pivotX = calL - s * 0.01f,
            pivotY = calB + s * 0.02f,
            angleDeg = -28f,
            color = accent.copy(alpha = 0.70f),
            s = s,
        )
        drawLeafSprig(
            pivotX = calR + s * 0.01f,
            pivotY = calB + s * 0.02f,
            angleDeg = 28f,
            color = accent.copy(alpha = 0.70f),
            s = s,
        )

        // ── Ground shadow (doodle@12%) ──────────────────────────────────────
        drawOval(
            color   = doodle.copy(alpha = 0.12f),
            topLeft = Offset(calL + s * 0.02f, calB + s * 0.01f),
            size    = Size(calW - s * 0.04f, s * 0.06f),
        )

        // ── Calendar body (surface / primaryContainer) ──────────────────────
        val bodyPath = Path().apply {
            addRoundRect(
                RoundRect(
                    left = calL, top = calT,
                    right = calR, bottom = calB,
                    radiusX = cr, radiusY = cr,
                )
            )
        }
        drawPath(bodyPath, color = if (isDark) primaryContainer else surface, style = Fill)

        // Thin right-side depth edge (darker primary)
        val edgeW = s * 0.018f
        drawRect(
            color   = primary.copy(alpha = 0.35f),
            topLeft = Offset(calR - edgeW, calT + cr),
            size    = Size(edgeW, calH - cr * 2f),
        )

        // Soft bottom inner shadow on page
        drawRect(
            color   = primary.copy(alpha = 0.06f),
            topLeft = Offset(calL + cr, calB - s * 0.025f),
            size    = Size(calW - cr * 2f, s * 0.025f),
        )

        // ── Purple header strip ─────────────────────────────────────────────
        val stripPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(calL, calT, calR, calT + stripH),
                    topLeft     = CornerRadius(cr),
                    topRight    = CornerRadius(cr),
                    bottomLeft  = CornerRadius(0f),
                    bottomRight = CornerRadius(0f),
                )
            )
        }
        drawPath(stripPath, color = primary, style = Fill)

        // Lighter sheen on top half of strip
        val sheenPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(calL, calT, calR, calT + stripH * 0.38f),
                    topLeft     = CornerRadius(cr),
                    topRight    = CornerRadius(cr),
                    bottomLeft  = CornerRadius(0f),
                    bottomRight = CornerRadius(0f),
                )
            )
        }
        drawPath(sheenPath, color = Color.White.copy(alpha = 0.10f), style = Fill)

        // ── Three binder rings (design.md: "three round binder rings") ──────
        val ringR      = s * 0.040f       // radius of each ring loop
        val ringStroke = s * 0.026f       // thickness
        val ringCY     = calT + s * 0.005f // centre Y at top edge of strip
        val ringXs     = listOf(
            calL + calW * 0.25f,
            calL + calW * 0.50f,
            calL + calW * 0.75f,
        )

        for (rx in ringXs) {
            // Upper semicircle (the loop visible above the strip)
            drawArc(
                color      = primary,
                startAngle = 180f,
                sweepAngle = 180f,        // clockwise from 9-o'clock through top to 3-o'clock
                useCenter  = false,
                topLeft    = Offset(rx - ringR, ringCY - ringR),
                size       = Size(ringR * 2f, ringR * 2f),
                style      = Stroke(width = ringStroke, cap = StrokeCap.Round),
            )
            // Lighter highlight on the left edge of the ring
            drawArc(
                color      = Color.White.copy(alpha = 0.35f),
                startAngle = 200f,
                sweepAngle = 55f,
                useCenter  = false,
                topLeft    = Offset(rx - ringR * 0.60f, ringCY - ringR * 0.60f),
                size       = Size(ringR * 1.20f, ringR * 1.20f),
                style      = Stroke(width = ringStroke * 0.35f, cap = StrokeCap.Round),
            )
        }

        // ── Plump heart on the page centre ──────────────────────────────────
        val heartCX   = calL + calW * 0.50f
        val heartCY   = calT + stripH + (calH - stripH) * 0.48f
        val heartSize = calW * 0.46f

        // Gradient: lighter upper-left highlight → solid primary
        val heartBrush = Brush.radialGradient(
            colors = listOf(
                primary.copy(alpha = 0.50f),
                primary,
            ),
            center = Offset(heartCX - heartSize * 0.12f, heartCY - heartSize * 0.14f),
            radius = heartSize * 0.80f,
        )
        drawHeart(heartCX, heartCY, heartSize, brush = heartBrush)

        // Small white gloss highlight top-left of heart
        drawHeart(
            heartCX - heartSize * 0.16f,
            heartCY - heartSize * 0.12f,
            heartSize * 0.18f,
            color = Color.White.copy(alpha = 0.30f),
        )

        // ── Floating hearts above (primary@60% and @35%) ────────────────────
        // Upper-left
        drawHeart(
            s * 0.12f, s * 0.10f,
            s * 0.09f,
            color = primary.copy(alpha = 0.60f),
        )
        // Upper-right (larger)
        drawHeart(
            s * 0.84f, s * 0.16f,
            s * 0.11f,
            color = primary.copy(alpha = 0.60f),
        )
        // Small one right of calendar
        drawHeart(
            s * 0.90f, s * 0.36f,
            s * 0.06f,
            color = primary.copy(alpha = 0.35f),
        )
    }
}

// ---------------------------------------------------------------------------
// Heart helper
// ---------------------------------------------------------------------------

/** Classic plump heart centred at ([cx],[cy]) with total [size] height. */
private fun heartPath(cx: Float, cy: Float, size: Float): Path {
    val r = size * 0.50f
    return Path().apply {
        // Bottom tip
        moveTo(cx, cy + r * 0.80f)
        // Left lobe → top centre dip
        cubicTo(
            cx - r * 1.15f, cy + r * 0.20f,
            cx - r * 1.15f, cy - r * 0.80f,
            cx,             cy - r * 0.30f,
        )
        // Right lobe → back to bottom tip
        cubicTo(
            cx + r * 1.15f, cy - r * 0.80f,
            cx + r * 1.15f, cy + r * 0.20f,
            cx,             cy + r * 0.80f,
        )
        close()
    }
}

private fun DrawScope.drawHeart(cx: Float, cy: Float, size: Float, color: Color) {
    drawPath(heartPath(cx, cy, size), color = color, style = Fill)
}

private fun DrawScope.drawHeart(cx: Float, cy: Float, size: Float, brush: Brush) {
    drawPath(heartPath(cx, cy, size), brush = brush, style = Fill)
}

// ---------------------------------------------------------------------------
// Leaf-sprig helper
// ---------------------------------------------------------------------------

/**
 * Five slim leaf shapes fanning from ([pivotX],[pivotY]) at [angleDeg].
 * Drawn with manual trig so it nests safely inside an outer [rotate] block.
 */
private fun DrawScope.drawLeafSprig(
    pivotX: Float,
    pivotY: Float,
    angleDeg: Float,
    color: Color,
    s: Float,
) {
    val leafW = s * 0.042f
    val leafH = s * 0.16f

    rotate(degrees = angleDeg, pivot = Offset(pivotX, pivotY)) {
        // Thin stem
        drawLine(
            color       = color.copy(alpha = 0.45f),
            start       = Offset(pivotX, pivotY),
            end         = Offset(pivotX, pivotY - leafH * 0.90f),
            strokeWidth = s * 0.007f,
            cap         = StrokeCap.Round,
        )

        for (i in -2..2) {
            val lx  = pivotX + i * leafW * 1.10f
            val hf  = 1.0f - kotlin.math.abs(i) * 0.08f
            val top = pivotY - leafH * hf

            val path = Path().apply {
                moveTo(lx, pivotY)
                cubicTo(
                    lx - leafW, top + leafH * 0.45f,
                    lx - leafW * 0.30f, top,
                    lx, top + leafH * 0.10f,
                )
                cubicTo(
                    lx + leafW * 0.30f, top,
                    lx + leafW, top + leafH * 0.45f,
                    lx, pivotY,
                )
                close()
            }
            // Outer leaves slightly lighter for depth
            val a = if (kotlin.math.abs(i) <= 1) 1.0f else 0.65f
            drawPath(path, color = color.copy(alpha = color.alpha * a), style = Fill)
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

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
