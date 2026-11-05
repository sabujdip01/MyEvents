package sabuj.m.myevents.feature.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.PaletteId
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.gradientChipColor
import sabuj.m.myevents.core.theme.gradientFor
import sabuj.m.myevents.core.theme.gradientIconColor
import sabuj.m.myevents.core.theme.numberL
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
 * Booking card matching Item E4 specifications:
 * Row 1: [Title] (left) | [Amount] paid (middle) | <edit> pencil icon (right)
 * Row 2: [Logo] (left)  | [tick/cross] bookingMade badge (right)
 * Row 3: [Logo] (left)  | Contract X. Paid X. Rem X. (middle) | [Icon] (right)
 * Row 4: Progress bar (middle) | [Icon] (right)
 * Row 5: <Name> (left)  | <Payment Due Chip> (middle) | <Delivery Date> (right)
 *
 * Single tap does not open edit (Item E6); edit is triggered exclusively by the pencil icon.
 */
@Composable
fun BookingCard(
    booking: Booking,
    slot: Int,
    editMode: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    event: Event? = null,
) {
    val isDark = AppColor.isDark
    val palette = PaletteId.A
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
            .padding(Spacing.md),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: [Title] (left) | [Amount] paid (middle) | <edit> pencil icon (right)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = booking.title,
                    style    = titleS,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text     = "${MoneyFormatter.format(booking.paidPaise)} paid",
                    style    = numberL,
                    color    = AppColor.primary,
                )
                Spacer(Modifier.width(Spacing.md))
                Icon(
                    imageVector        = Icons.Rounded.Edit,
                    contentDescription = "Edit booking",
                    tint               = AppColor.primary,
                    modifier           = Modifier.size(20.dp).clickable { onEditClick() },
                )
            }

            Spacer(Modifier.height(Spacing.xs))

            // Body Rows with Event Logo Left
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Left Logo (48 dp)
                IconChip(
                    icon               = event?.icon?.fallback ?: Icons.Rounded.Delete,
                    contentDescription = event?.title ?: "",
                    size               = IconChipSize.Default,
                    containerColor     = chipColor,
                    iconColor          = iconColor,
                    customRes          = event?.icon?.customRes,
                )

                Spacer(Modifier.width(Spacing.md))

                // Middle Content Column
                Column(modifier = Modifier.weight(1f)) {
                    // Row 2 Right: [tick/cross] badge
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Pill(
                            text    = if (booking.bookingMade) "Booking Made" else "Pending",
                            variant = if (booking.bookingMade) PillVariant.Success else PillVariant.Warning,
                        )
                    }

                    Spacer(Modifier.height(2.dp))

                    // Row 3 Middle: Contract X. Paid X. Rem X.
                    Text(
                        text     = "Contract ${MoneyFormatter.format(booking.totalAmountPaise)} • Rem. ${MoneyFormatter.format(booking.amountLeftPaise)}",
                        style    = bodyM,
                        color    = AppColor.text2,
                        maxLines = 1,
                    )

                    Spacer(Modifier.height(Spacing.xs))

                    // Row 4 Middle: Progress Bar
                    LinearProgressIndicator(
                        progress   = { progress },
                        modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color      = AppColor.accent,
                        trackColor = AppColor.surface.copy(alpha = 0.4f),
                    )
                }

                Spacer(Modifier.width(Spacing.sm))

                // Right: Booking Icon Chip
                IconChip(
                    icon               = booking.bookingIcon.fallback,
                    contentDescription = booking.bookingType,
                    size               = IconChipSize.Sm,
                    containerColor     = chipColor,
                    iconColor          = iconColor,
                    customRes          = booking.bookingIcon.customRes,
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            // Row 5: <Name> (left) | <Payment Due Chip> (middle) | <Delivery Date> (right)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = booking.contactName.ifBlank { "Vendor" },
                    style    = bodyM,
                    color    = AppColor.text2,
                    modifier = Modifier.weight(1f, fill = false),
                )

                Spacer(Modifier.width(Spacing.xs))

                StatusPill(booking = booking, today = today)

                Spacer(Modifier.weight(1f))

                if (booking.nextDueDate != null) {
                    Text(
                        text  = "Due: ${DateUtils.formatShort(booking.nextDueDate)}",
                        style = bodyM,
                        color = AppColor.text2,
                    )
                }
            }
        }

        // Delete badge in edit mode
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

/** Status pill logic for bookings */
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
