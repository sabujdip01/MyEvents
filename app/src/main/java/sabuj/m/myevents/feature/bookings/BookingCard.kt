package sabuj.m.myevents.feature.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            .padding(start = Spacing.md, end = Spacing.md, top = Spacing.xs, bottom = Spacing.md),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: [Booking Icon 50% enlarged] | <Event Name> (left) | ₹<Amount Paid> paid (middle) | [Edit Icon] (right)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Booking Icon (50% enlarged, transparent background)
                IconChip(
                    icon               = booking.bookingIcon.fallback,
                    contentDescription = booking.bookingType,
                    size               = IconChipSize.Lg,
                    containerColor     = Color.Transparent,
                    iconColor          = iconColor,
                    customRes          = booking.bookingIcon.customRes,
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text     = event?.title ?: booking.title,
                    style    = titleS,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text      = "${MoneyFormatter.format(booking.paidPaise)} paid",
                    style     = titleS,
                    color     = AppColor.text,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.weight(1f),
                )
                Icon(
                    imageVector        = Icons.Rounded.Edit,
                    contentDescription = "Edit booking",
                    tint               = AppColor.primary,
                    modifier           = Modifier.size(22.dp).clickable { onEditClick() },
                )
            }

            Spacer(Modifier.height(Spacing.xs))

            // Row 2: [Event Icon 50% enlarged] | Contract ₹X . Remaining ₹Y
            //                                   Progress Bar
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Event Icon (50% enlarged, transparent background)
                IconChip(
                    icon               = event?.icon?.fallback ?: Icons.Rounded.Delete,
                    contentDescription = event?.title ?: "",
                    size               = IconChipSize.Lg,
                    containerColor     = Color.Transparent,
                    iconColor          = iconColor,
                    customRes          = event?.icon?.customRes,
                )

                Spacer(Modifier.width(Spacing.xs))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text     = "Contract ${MoneyFormatter.format(booking.totalAmountPaise)}   •   Remaining ${MoneyFormatter.format(booking.amountLeftPaise)}",
                        style    = bodyM,
                        color    = AppColor.text2,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    LinearProgressIndicator(
                        progress   = { progress },
                        modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color      = AppColor.accent,
                        trackColor = AppColor.surface.copy(alpha = 0.4f),
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            // Row 3: Vendor Name (col 1: 0%) | Amount Left (col 2: 26%) | Due Date (col 3: 52%) | Booking Confirmed (col 4: Right)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val totalWidth = maxWidth

                // Col 1: Vendor Name (0% - fully left aligned)
                Text(
                    text     = booking.contactName.ifBlank { "Vendor" },
                    style    = bodyM,
                    color    = AppColor.text2,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterStart),
                )

                // Col 2: Amount left chip at 26% relative position (defined red color PillVariant.Danger)
                val amountLeftText = if (booking.isFullyPaid) "Fully Paid" else "${MoneyFormatter.format(booking.amountLeftPaise)} left"
                Box(modifier = Modifier.padding(start = totalWidth * 0.26f).align(Alignment.CenterStart)) {
                    Pill(text = amountLeftText, variant = PillVariant.Danger)
                }

                // Col 3: Due date chip at 52% relative position (if available)
                if (booking.nextDueDate != null) {
                    val isOverdue = booking.nextDueDate.isBefore(today)
                    Box(modifier = Modifier.padding(start = totalWidth * 0.52f).align(Alignment.CenterStart)) {
                        Pill(
                            text    = "Due ${DateUtils.formatShort(booking.nextDueDate)}",
                            variant = if (isOverdue) PillVariant.Danger else PillVariant.Warning,
                        )
                    }
                }

                // Col 4: Booking confirmation status (fully right aligned)
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Pill(
                        text    = if (booking.bookingMade) "Booking Confirmed" else "Not Confirmed",
                        variant = if (booking.bookingMade) PillVariant.Success else PillVariant.Warning,
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
