package sabuj.m.myevents.domain.model

import java.time.LocalDate

/**
 * Domain model for a single payment made against a [Booking].
 *
 * @param id         Unique UUID string.
 * @param bookingId  The owning booking's id.
 * @param date       Date the payment was made.
 * @param amountPaise Amount paid in paise.
 * @param note       Optional note about this payment.
 */
data class BookingPayment(
    val id: String,
    val bookingId: String,
    val date: LocalDate,
    val amountPaise: Long,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long,
)
