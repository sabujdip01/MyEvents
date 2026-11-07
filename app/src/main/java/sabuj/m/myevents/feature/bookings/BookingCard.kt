package sabuj.m.myevents.feature.bookings

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.AppTheme
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
            .padding(start = Spacing.md, end = Spacing.md, top = 4.dp, bottom = Spacing.md),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: [Booking Icon 30% reduced, flush left] | <Booking Title> | ₹<Amount Paid> paid | [Edit Icon] (right)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Booking Icon (32dp icon, transparent background, flush left)
                if (booking.bookingIcon.customRes != null) {
                    Image(
                        painter            = painterResource(booking.bookingIcon.customRes!!),
                        contentDescription = booking.bookingType,
                        modifier           = Modifier.size(32.dp),
                        contentScale       = ContentScale.Fit,
                    )
                } else {
                    Icon(
                        imageVector        = booking.bookingIcon.fallback,
                        contentDescription = booking.bookingType,
                        tint               = iconColor,
                        modifier           = Modifier.size(32.dp),
                    )
                }
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text      = booking.title,
                    style     = titleS,
                    color     = AppColor.text,
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.weight(1f),
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text     = "${MoneyFormatter.format(booking.paidPaise)} paid",
                    style    = titleS,
                    color    = AppColor.text,
                    maxLines = 1,
                )
                Spacer(Modifier.width(Spacing.xs))
                if (editMode) {
                    Icon(
                        imageVector        = Icons.Rounded.Delete,
                        contentDescription = "Delete booking",
                        tint               = AppColor.danger,
                        modifier           = Modifier
                            .size(20.dp)
                            .clickable { onDeleteClick() },
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Rounded.Edit,
                        contentDescription = "Edit booking",
                        tint               = AppColor.primary,
                        modifier           = Modifier
                            .size(20.dp)
                            .clickable { onEditClick() },
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            // Row 2: [Event Icon 30% reduced, flush left] | Contract ₹X . Remaining ₹Y
            //                                               Progress Bar
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val eventCustomRes = event?.icon?.customRes
                val eventFallback = event?.icon?.fallback ?: Icons.Rounded.Delete
                if (eventCustomRes != null) {
                    Image(
                        painter            = painterResource(eventCustomRes),
                        contentDescription = event?.title ?: "",
                        modifier           = Modifier.size(30.dp),
                        contentScale       = ContentScale.Fit,
                    )
                } else {
                    Icon(
                        imageVector        = eventFallback,
                        contentDescription = event?.title ?: "",
                        tint               = iconColor,
                        modifier           = Modifier.size(32.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text     = "Contract ${MoneyFormatter.format(booking.totalAmountPaise)}   •   Remaining ${MoneyFormatter.format(booking.amountLeftPaise)}",
                        style    = bodyM,
                        color    = AppColor.text2,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(7.dp))
                    LinearProgressIndicator(
                        progress   = { progress },
                        modifier   = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(4.dp)),
                        color      = AppColor.accent,
                        trackColor = AppColor.surface.copy(alpha = 0.4f),
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Row 3: Vendor Name (col 1: 0%) | Amount Left (col 2: 26%) | Due Date (col 3: 54%) | Booking Confirmed (col 4: Right)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val totalWidth = maxWidth

                // Col 1: Vendor Name (0% - fully left aligned, show blank if empty)
                Text(
                    text     = booking.contactName,
                    style    = bodyM,
                    color    = AppColor.text2,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterStart),
                )

                // Col 2: Amount left chip at 26% relative position (Fully Paid in green Success, else red Danger)
                val amountLeftVariant = if (booking.isFullyPaid) PillVariant.Success else PillVariant.Danger
                val amountLeftText = if (booking.isFullyPaid) "Fully Paid" else "${MoneyFormatter.format(booking.amountLeftPaise)} left"
                Box(modifier = Modifier.padding(start = totalWidth * 0.26f).align(Alignment.CenterStart)) {
                    Pill(text = amountLeftText, variant = amountLeftVariant)
                }

                // Col 3: Due date chip — positioned at 54% between Amount Left (26%) and Booking Confirmed (right)
                Box(modifier = Modifier.padding(start = totalWidth * 0.48f).align(Alignment.CenterStart)) {
                    if (booking.nextDueDate != null) {
                        val isOverdue = booking.nextDueDate.isBefore(today)
                        Pill(
                            text    = "Due ${DateUtils.formatShort(booking.nextDueDate)}",
                            variant = if (isOverdue) PillVariant.Danger else PillVariant.Warning,
                        )
                    } else {
                        // Placeholder — holds exact space when no due date is set
                        Spacer(Modifier.width(72.dp).height(24.dp))
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
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@LightDarkPreviews
@Composable
private fun BookingCardPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            BookingCard(
                booking      = PreviewData.sampleBooking,
                slot         = 0,
                editMode     = false,
                onEditClick  = {},
                onDeleteClick = {},
                event        = PreviewData.sampleEvents[1],
            )
        }
    }
}

@LightDarkPreviews
@Composable
private fun BookingCardEditModePreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            BookingCard(
                booking      = PreviewData.sampleBooking,
                slot         = 1,
                editMode     = true,
                onEditClick  = {},
                onDeleteClick = {},
                event        = PreviewData.sampleEvents[1],
            )
        }
    }
}
