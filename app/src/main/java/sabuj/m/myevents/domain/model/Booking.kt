package sabuj.m.myevents.domain.model

import sabuj.m.myevents.core.icons.BookingIcon
import java.time.LocalDate

/**
 * Domain model for a vendor booking.
 *
 * @param id                Unique UUID string.
 * @param eventId           The owning event's id.
 * @param title             Custom label for this booking (e.g. "Photography").
 * @param bookingType       Category name (e.g. "Camera", "Mandap").
 * @param bookingIcon       The [BookingIcon] for the card icon.
 * @param contactName       Contact person name.
 * @param phone             Contact phone number.
 * @param totalAmountPaise  Total agreed amount in paise.
 * @param nextDueDate       Optional date of the next payment instalment.
 * @param payments          All payment records against this booking.
 */
data class Booking(
    val id: String,
    val eventId: String,
    val title: String,
    val bookingType: String,
    val bookingIcon: BookingIcon,
    val contactName: String,
    val phone: String,
    /** Total agreed amount in paise. */
    val totalAmountPaise: Long,
    /** Optional next payment due date. */
    val nextDueDate: LocalDate?,
    val bookingMade: Boolean = true,
    val payments: List<BookingPayment>,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: SyncState,
    val deleted: Boolean,
) {
    /** Sum of all payment amounts in paise. */
    val paidPaise: Long get() = payments.sumOf { it.amountPaise }

    /** Remaining unpaid amount in paise. */
    val amountLeftPaise: Long get() = totalAmountPaise - paidPaise

    /** True when the booking is fully paid. */
    val isFullyPaid: Boolean get() = amountLeftPaise <= 0
}
