package sabuj.m.myevents.core.ui.doodles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

/**
 * 3D-style desk-calendar illustration drawn entirely with Compose Canvas.
 * Coordinates use the supplied 1280 x 1180 reference image as the design grid.
 * The composition deliberately keeps the large open area above the illustration.
 */
@Composable
fun HeroIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
) {
    val isDark = sabuj.m.myevents.core.theme.AppColor.isDark
    Canvas(modifier = modifier.width(size).aspectRatio(1280f / 1180f)) {
        scale(
            scaleX = this.size.width / 1280f,
            scaleY = this.size.height / 1180f,
            pivot = Offset.Zero,
        ) {
            drawCalendarReference(isDark)
        }
    }
}

private val Purple = Color(0xFF8B4BEF)
private val PurpleDeep = Color(0xFF6424D8)
private val PurpleMid = Color(0xFFA875F4)
private val PurplePale = Color(0xFFD2BDFB)
private val LeafColor = Color(0xFFB49AF1)

private fun DrawScope.drawCalendarReference(isDark: Boolean) {
    val background = if (isDark) Color(0xFF171321) else Color(0xFFFAF9FF)
    val bubble = if (isDark) Color(0xFF9A7BE7).copy(alpha = 0.075f)
                 else Color(0xFFE9E3FF).copy(alpha = 0.40f)
    drawRect(background)

    // Soft, oversized backdrop circles from the reference.
    drawCircle(bubble, 245f, Offset(238f, 319f))
    drawCircle(bubble.copy(alpha = bubble.alpha * 0.82f), 275f, Offset(663f, 583f))
    drawCircle(bubble.copy(alpha = bubble.alpha * 0.78f), 168f, Offset(54f, 553f))
    drawCircle(bubble.copy(alpha = bubble.alpha * 0.68f), 266f, Offset(1068f, 1130f))

    // Ambient shadow beneath the calendar.
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF7E52D5).copy(alpha = if (isDark) 0.22f else 0.15f), Color.Transparent),
            center = Offset(486f, 1080f),
            radius = 315f,
        ),
        topLeft = Offset(105f, 1025f),
        size = Size(765f, 112f),
    )

    // Leaves are behind the calendar and sit low around its two sides.
    drawLeftBranch()
    drawRightBranch()

    // Rear purple cover: the thick visible left spine and lower-left bevel.
    val rear = Path().apply {
        moveTo(310f, 604f)
        cubicTo(288f, 606f, 278f, 624f, 276f, 650f)
        lineTo(266f, 1016f)
        cubicTo(264f, 1050f, 280f, 1068f, 310f, 1070f)
        lineTo(654f, 1045f)
        cubicTo(682f, 1043f, 692f, 1024f, 690f, 995f)
        lineTo(678f, 622f)
        close()
    }
    drawPath(
        rear,
        Brush.linearGradient(
            colors = listOf(Color(0xFF7D38E9), Color(0xFF6925DD), Color(0xFF8D4AF0)),
            start = Offset(278f, 810f), end = Offset(688f, 830f),
        ),
    )
    // Bright narrow bevel along the left spine.
    drawPath(
        Path().apply {
            moveTo(306f, 617f)
            cubicTo(287f, 620f, 284f, 638f, 282f, 665f)
            lineTo(273f, 1005f)
            cubicTo(273f, 1038f, 287f, 1055f, 309f, 1060f)
            lineTo(326f, 1047f)
            lineTo(322f, 630f)
            close()
        },
        Brush.horizontalGradient(listOf(Color(0xFF5F1FD4), Color(0xFF9B5AF4), Color(0xFF7D37E8))),
    )

    // Calendar page: a slightly skewed front plane, not a flat rectangle.
    val page = Path().apply {
        moveTo(302f, 699f)
        lineTo(682f, 695f)
        lineTo(702f, 1016f)
        cubicTo(704f, 1042f, 690f, 1057f, 666f, 1059f)
        lineTo(359f, 1073f)
        cubicTo(333f, 1074f, 322f, 1057f, 319f, 1033f)
        lineTo(300f, 727f)
        cubicTo(298f, 712f, 297f, 704f, 302f, 699f)
        close()
    }
    // Soft drop shadow offset just below/right of the paper.
    drawPath(page, Color(0xFF5630A8).copy(alpha = if (isDark) 0.24f else 0.09f), style = Stroke(width = 15f, cap = StrokeCap.Round))
    drawPath(
        page,
        Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFFFFEFF), Color(0xFFF8F6FF)),
            start = Offset(320f, 700f), end = Offset(685f, 1065f),
        ),
    )
    // Subtle paper edge shading along right and bottom edges.
    drawPath(
        Path().apply {
            moveTo(682f, 700f)
            lineTo(702f, 1016f)
            cubicTo(704f, 1042f, 690f, 1057f, 666f, 1059f)
            lineTo(359f, 1073f)
        },
        Color(0xFFB9A5E8).copy(alpha = 0.26f),
        style = Stroke(width = 7f, cap = StrokeCap.Round),
    )
    drawPath(
        Path().apply {
            moveTo(327f, 1045f)
            cubicTo(333f, 1062f, 344f, 1069f, 364f, 1068f)
            lineTo(665f, 1054f)
        },
        Color.White.copy(alpha = 0.85f),
        style = Stroke(width = 5f, cap = StrokeCap.Round),
    )

    // Four thick, rounded binder loops. Layered strokes simulate a glossy 3D tube.
    val ringCenters = listOf(390f, 477f, 548f, 615f)
    ringCenters.forEachIndexed { index, x ->
        val y = 603f + if (index == 1 || index == 2) -2f else 0f
        val ring = Path().apply {
            moveTo(x - 25f, y + 36f)
            cubicTo(x - 39f, y - 30f, x - 12f, y - 49f, x + 7f, y - 43f)
            cubicTo(x + 35f, y - 36f, x + 34f, y + 5f, x + 23f, y + 38f)
            cubicTo(x + 17f, y + 58f, x - 3f, y + 64f, x - 12f, y + 48f)
        }
        // Deep soft underside, purple tube, and specular highlight.
        drawPath(ring, Color(0xFF5420C8).copy(alpha = 0.24f), style = Stroke(width = 29f, cap = StrokeCap.Round))
        drawPath(
            ring,
            Brush.linearGradient(
                colors = listOf(Color(0xFFC7A8FF), Color(0xFF9B63F1), Color(0xFF6B2DDB), Color(0xFF4E16C7)),
                start = Offset(x - 25f, y - 43f), end = Offset(x + 20f, y + 62f),
            ),
            style = Stroke(width = 22f, cap = StrokeCap.Round),
        )
        drawPath(
            ring,
            Color.White.copy(alpha = 0.38f),
            style = Stroke(width = 4.5f, cap = StrokeCap.Round),
        )
    }

    // Header band has rounded top corners and a straight lower edge.
    val header = Path().apply {
        moveTo(329f, 600f)
        cubicTo(340f, 596f, 351f, 596f, 366f, 596f)
        lineTo(650f, 596f)
        cubicTo(672f, 596f, 684f, 611f, 685f, 632f)
        lineTo(688f, 695f)
        lineTo(303f, 700f)
        lineTo(287f, 634f)
        cubicTo(283f, 615f, 299f, 603f, 329f, 600f)
        close()
    }
    drawPath(
        header,
        Brush.linearGradient(
            colors = listOf(Color(0xFFB58CFA), Color(0xFF9B63F1), Color(0xFF8C4BEF), Color(0xFF8750E9)),
            start = Offset(320f, 595f), end = Offset(680f, 700f),
        ),
    )
    // Soft glossy bloom over the upper part of the band.
    drawPath(
        Path().apply {
            moveTo(323f, 604f)
            cubicTo(350f, 593f, 626f, 593f, 660f, 600f)
            cubicTo(676f, 604f, 682f, 615f, 683f, 632f)
            lineTo(685f, 653f)
            cubicTo(570f, 643f, 420f, 646f, 294f, 657f)
            lineTo(289f, 632f)
            cubicTo(288f, 617f, 300f, 608f, 323f, 604f)
            close()
        },
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.23f), Color.White.copy(alpha = 0.04f), Color.Transparent), 595f, 660f),
    )
    // A fine saturated lower lip gives the header thickness.
    drawLine(Color(0xFF7132DF).copy(alpha = 0.34f), Offset(303f, 699f), Offset(687f, 695f), 5f, cap = StrokeCap.Round)

    // Binder ends in front of the header: four small curled tips visible below the arches.
    ringCenters.forEachIndexed { index, x ->
        val y = 617f + if (index == 1 || index == 2) -2f else 0f
        val tail = Path().apply {
            moveTo(x + 22f, y - 2f)
            cubicTo(x + 25f, y + 22f, x + 16f, y + 44f, x + 2f, y + 49f)
            cubicTo(x - 10f, y + 53f, x - 16f, y + 43f, x - 12f, y + 31f)
        }
        drawPath(tail, Color(0xFF5E1BCC).copy(alpha = 0.20f), style = Stroke(width = 25f, cap = StrokeCap.Round))
        drawPath(tail, Brush.linearGradient(listOf(Color(0xFFB68CFF), Color(0xFF8B4BEF), Color(0xFF5E1BD0)), Offset(x - 12f, y), Offset(x + 22f, y + 50f)), style = Stroke(width = 19f, cap = StrokeCap.Round))
        drawPath(tail, Color.White.copy(alpha = 0.30f), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
    }

    // Large raised heart centered on the white page.
    val heart = heartPath(523f, 875f, 154f)
    drawPath(heart, Color(0xFF5B2AB8).copy(alpha = 0.18f), style = Stroke(width = 13f, cap = StrokeCap.Round))
    drawPath(
        heart,
        Brush.radialGradient(
            colors = listOf(Color(0xFFC6A8FF), Color(0xFFA77AF6), Color(0xFF8D55EF), Color(0xFF7739E4)),
            center = Offset(484f, 833f), radius = 150f,
        ),
    )
    drawPath(
        heartPath(490f, 833f, 31f),
        Color.White.copy(alpha = 0.13f),
    )

    // Floating glossy hearts at the same relative positions as the reference.
    drawFloatingHeart(241f, 554f, 66f)
    drawFloatingHeart(780f, 685f, 82f)
}

private fun heartPath(cx: Float, cy: Float, height: Float): Path {
    val r = height * 0.5f
    return Path().apply {
        moveTo(cx, cy + r * 0.82f)
        cubicTo(cx - r * 1.15f, cy + r * 0.18f, cx - r * 1.18f, cy - r * 0.78f, cx, cy - r * 0.28f)
        cubicTo(cx + r * 1.18f, cy - r * 0.78f, cx + r * 1.15f, cy + r * 0.18f, cx, cy + r * 0.82f)
        close()
    }
}

private fun DrawScope.drawFloatingHeart(cx: Float, cy: Float, height: Float) {
    val path = heartPath(cx, cy, height)
    drawPath(path, Color(0xFF6C35D1).copy(alpha = 0.14f), style = Stroke(width = 7f))
    drawPath(
        path,
        Brush.radialGradient(
            colors = listOf(Color(0xFFD7C2FF), Color(0xFFB58CF7), Color(0xFF965DF0), Color(0xFF7439E1)),
            center = Offset(cx - height * 0.20f, cy - height * 0.25f),
            radius = height * 0.82f,
        ),
    )
    drawPath(heartPath(cx - height * 0.20f, cy - height * 0.27f, height * 0.17f), Color.White.copy(alpha = 0.17f))
}

private fun DrawScope.drawLeaf(base: Offset, tip: Offset, width: Float, color: Color, bend: Float) {
    val leaf = Path().apply {
        moveTo(base.x, base.y)
        cubicTo(base.x + bend * width, base.y - width * 0.52f, tip.x + bend * width * 0.58f, tip.y + width * 0.52f, tip.x, tip.y)
        cubicTo(tip.x - bend * width * 0.54f, tip.y - width * 0.45f, base.x - bend * width * 0.42f, base.y - width * 0.26f, base.x, base.y)
        close()
    }
    drawPath(
        leaf,
        Brush.linearGradient(
            colors = listOf(Color(0xFFD9C9FF).copy(alpha = color.alpha), color),
            start = Offset(tip.x - width * 0.4f, tip.y), end = base,
        ),
    )
}

private fun DrawScope.drawLeftBranch() {
    val base = Offset(224f, 914f)
    drawLine(LeafColor.copy(alpha = 0.62f), base, Offset(165f, 719f), 5f, cap = StrokeCap.Round)
    drawLeaf(Offset(194f, 826f), Offset(134f, 711f), 38f, LeafColor.copy(alpha = 0.82f), -1.1f)
    drawLeaf(Offset(205f, 872f), Offset(102f, 839f), 37f, LeafColor.copy(alpha = 0.78f), -1.0f)
    drawLeaf(Offset(213f, 895f), Offset(145f, 968f), 34f, LeafColor.copy(alpha = 0.82f), -0.8f)
    drawLeaf(Offset(220f, 907f), Offset(107f, 900f), 35f, LeafColor.copy(alpha = 0.78f), -1.0f)
    drawLeaf(Offset(205f, 858f), Offset(269f, 798f), 32f, LeafColor.copy(alpha = 0.76f), 0.9f)
}

private fun DrawScope.drawRightBranch() {
    val base = Offset(699f, 1066f)
    drawLine(LeafColor.copy(alpha = 0.66f), base, Offset(835f, 856f), 5f, cap = StrokeCap.Round)
    drawLeaf(Offset(731f, 1014f), Offset(714f, 935f), 32f, LeafColor.copy(alpha = 0.82f), -0.6f)
    drawLeaf(Offset(753f, 978f), Offset(771f, 862f), 35f, LeafColor.copy(alpha = 0.82f), 0.4f)
    drawLeaf(Offset(786f, 928f), Offset(895f, 831f), 37f, LeafColor.copy(alpha = 0.84f), 0.8f)
    drawLeaf(Offset(783f, 949f), Offset(896f, 956f), 34f, LeafColor.copy(alpha = 0.78f), 0.8f)
    drawLeaf(Offset(740f, 1028f), Offset(831f, 1053f), 32f, LeafColor.copy(alpha = 0.78f), 0.8f)
}

@LightDarkPreviews
@Composable
private fun HeroIllustrationPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize().padding(Spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            HeroIllustration(size = 360.dp)
        }
    }
}
