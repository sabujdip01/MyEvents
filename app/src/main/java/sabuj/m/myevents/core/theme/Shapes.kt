package sabuj.m.myevents.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner-radius tokens from design.md section 2.2.
 * Use these directly in composables for consistent rounding.
 */
object Radius {
    val chip   = 12.dp
    val field  = 14.dp
    val icon   = 14.dp
    val card   = 20.dp
    val hero   = 24.dp
    val fab    = 18.dp
    val sheet  = 28.dp // top corners only
    val dialog = 28.dp
    val pill   = 999.dp
}

/** Material 3 Shapes wired to our radius tokens. */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.chip),
    small      = RoundedCornerShape(Radius.field),
    medium     = RoundedCornerShape(Radius.card),
    large      = RoundedCornerShape(Radius.hero),
    extraLarge = RoundedCornerShape(Radius.sheet),
)
