package sabuj.m.myevents.feature.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.gradientChipColor
import sabuj.m.myevents.core.theme.gradientFor
import sabuj.m.myevents.core.theme.gradientIconColor
import sabuj.m.myevents.core.theme.numberL
import sabuj.m.myevents.core.theme.PaletteId
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipDanger
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate

/**
 * Booking card for the Bookings list.
 *
 * Layout (≈148 dp tall):
 * - Top band (32 dp): booking title [titleS]
 * - Body row: event icon chip (left 15%), money block (middle 75%), booking icon (right 10%)
 * - Money block: paid amount [numberL], "of ₹total" [bodyM text2], progress bar, contact + status pill
 *
 * In [editMode] a danger delete badge appears at top-right.
 *
 * @param booking       The booking domain model to display.
 * @param slot          Index (0–5) used for the gradient colour.
 * @param editMode      True when the screen is in edit mode (delete badge shown).
 * @param onTap         Called on normal tap — opens edit sheet.
 * @param onDeleteClick Called when the delete badge is tapped.
 * @param event         The parent event (for its icon in the left chip). May be null.
 */
@Composable
fun BookingCard(
    booking: Booking,
    slot: Int,
    editMode: Boolean,
    onTap: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    event: Event? = null,
) {
    val isDark = AppColor.isDark
    val palette = PaletteId.A // TODO: read from settings when palette preference is wired
    val gradient: Brush = remember(slot, isDark) { gradientFor(slot, palette, isDark) }
    val chipColor = remember(slot, isDark) { gradientChipColor(slot, palette, isDark) }
    val iconColor = remember(slot, isDark) { gradientIconColor(slot, palette, isDark) }

    val today = remember { LocalDate.now() }
    val progress = if (booking.totalAmountPaise > 0)
        (booking.paidPaise.toFloat() / booking.totalAmountPaise.toFloat()).coerceIn(0f, 1f)
    else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.card))
            .background(gradient)
            .clickable { onTap() }
            .padding(bottom = Spacing.md),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ── Title band ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(horizontal = Spacing.md),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text  = booking.title,
                    style = titleS,
                    color = AppColor.text,
                )
            }

            // ── Body row ────────────────────────────────────────────────────
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Left: event icon chip (15%)
                IconChip(
                    icon               = event?.icon?.fallback
                        ?: Icons.Rounded.Delete, // fallback if no event
                    contentDescription = event?.title ?: "",
                    size               = IconChipSize.Default,
                    containerColor     = chipColor,
                    iconColor          = iconColor,
                )

                Spacer(Modifier.width(Spacing.md))

                // Middle: money block (fills remaining space)
                Column(modifier = Modifier.weight(1f)) {
                    // Paid amount
                    Text(
                        text  = "${MoneyFormatter.format(booking.paidPaise)} paid",
                        style = numberL,
                        color = AppColor.text,
                    )
                    // Contract & Remaining
                    Text(
                        text  = "Contract ${MoneyFormatter.format(booking.totalAmountPaise)} • Rem. ${MoneyFormatter.format(booking.amountLeftPaise)}",
                        style = bodyM,
                        color = AppColor.text2,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    // Progress bar
                    LinearProgressIndicator(
                        progress           = { progress },
                        modifier           = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color              = AppColor.accent,
                        trackColor         = AppColor.surface.copy(alpha = 0.4f),
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    // Contact name + status pill
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (booking.contactName.isNotBlank()) {
                            Text(
                                text     = booking.contactName,
                                style    = bodyM,
                                color    = AppColor.text2,
                                modifier = Modifier.weight(1f, fill = false),
                            )
                            Spacer(Modifier.width(Spacing.xs))
                        }
                        StatusPill(booking = booking, today = today)
                    }
                }

                Spacer(Modifier.width(Spacing.sm))

                // Right: booking icon (10%)
                IconChip(
                    icon               = booking.bookingIcon.fallback,
                    contentDescription = booking.bookingType,
                    size               = IconChipSize.Sm,
                    containerColor     = chipColor,
                    iconColor          = iconColor,
                )
            }
        }

        // ── Edit-mode delete badge ───────────────────────────────────────
        if (editMode) {
            IconChipDanger(
                icon               = Icons.Rounded.Delete,
                contentDescription = "Delete booking",
                size               = IconChipSize.Sm,
                modifier           = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-Spacing.xs), y = Spacing.xs)
                    .clickable { onDeleteClick() },
            )
        }
    }
}

/**
 * Status pill logic:
 * - Fully paid → Success "Paid"
 * - Overdue (nextDueDate < today, not fully paid) → Danger "Overdue"
 * - Due within 7 days or partially paid → Warning "Due DD MMM"
 * - Otherwise → Default "₹X left"
 */
@Composable
private fun StatusPill(booking: Booking, today: LocalDate) {
    when {
        booking.isFullyPaid -> Pill("Paid", variant = PillVariant.Success)
        booking.nextDueDate != null && booking.nextDueDate.isBefore(today) ->
            Pill("Overdue", variant = PillVariant.Danger)
        booking.nextDueDate != null && !booking.nextDueDate.isAfter(today.plusDays(7)) ->
            Pill("Due ${DateUtils.formatShort(booking.nextDueDate)}", variant = PillVariant.Warning)
        booking.paidPaise > 0 ->
            Pill("Due ${booking.nextDueDate?.let { DateUtils.formatShort(it) } ?: ""}", variant = PillVariant.Warning)
        else ->
            Pill(MoneyFormatter.format(booking.amountLeftPaise) + " left", variant = PillVariant.Default)
    }
}
