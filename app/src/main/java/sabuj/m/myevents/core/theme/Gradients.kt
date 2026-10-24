package sabuj.m.myevents.core.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Gradient hue tables from design.md section 3.4.
 * 6 slots per palette — cards of the same type get a different slot
 * index so they always differ visually.
 */
private val gradientHues: Map<PaletteId, IntArray> = mapOf(
    PaletteId.A to intArrayOf(268, 290, 248, 310, 228, 200),
    PaletteId.B to intArrayOf(357, 340,  12, 200, 185, 320),
    PaletteId.C to intArrayOf(226, 204, 248, 190, 270, 170),
    PaletteId.D to intArrayOf(305, 330, 285, 350, 265, 245),
)

/**
 * Returns the card gradient [Brush] for the given [slot] (0–5),
 * [palette] and [isDark] flag.
 *
 * Formula (design.md 3.4):
 *  Light → start hsl(h, 85%, 93%)  end hsl(h+15, 80%, 85%)
 *  Dark  → start hsl(h, 45%, 24%)  end hsl(h+15, 50%, 15%)
 *
 * Direction: 135° (top-left → bottom-right).
 */
fun gradientFor(slot: Int, palette: PaletteId, isDark: Boolean): Brush {
    val hues = gradientHues[palette] ?: gradientHues[PaletteId.A]!!
    val h = hues[slot.coerceIn(0, 5)].toFloat()
    val start: Color
    val end: Color
    if (!isDark) {
        start = Color.hsl(h,       0.85f, 0.93f)
        end   = Color.hsl(h + 15f, 0.80f, 0.85f)
    } else {
        start = Color.hsl(h,       0.45f, 0.24f)
        end   = Color.hsl(h + 15f, 0.50f, 0.15f)
    }
    return Brush.linearGradient(
        colors = listOf(start, end),
        start  = Offset(0f, 0f),
        end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )
}

/**
 * Returns the icon-chip background colour for a gradient card
 * (design.md 3.4 — used instead of primaryContainer on gradient surfaces).
 */
fun gradientChipColor(slot: Int, palette: PaletteId, isDark: Boolean): Color {
    val hues = gradientHues[palette] ?: gradientHues[PaletteId.A]!!
    val h = hues[slot.coerceIn(0, 5)].toFloat()
    return if (!isDark) Color.hsl(h, 0.70f, 0.45f, alpha = 0.20f)
    else                Color.hsl(h, 0.80f, 0.70f, alpha = 0.20f)
}

/**
 * Returns the icon colour on a gradient card chip (full opacity).
 */
fun gradientIconColor(slot: Int, palette: PaletteId, isDark: Boolean): Color {
    val hues = gradientHues[palette] ?: gradientHues[PaletteId.A]!!
    val h = hues[slot.coerceIn(0, 5)].toFloat()
    return if (!isDark) Color.hsl(h, 0.70f, 0.45f)
    else                Color.hsl(h, 0.80f, 0.70f)
}
