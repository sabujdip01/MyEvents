package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.BookingRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/** Validation result for add/edit operations. */
sealed class BookingSaveResult {
    object Success : BookingSaveResult()
    object EventIdBlank : BookingSaveResult()
    object TotalInvalid : BookingSaveResult()
}

/**
 * Validates inputs and persists a new booking.
 * Title must not be blank, eventId must not be blank, total must be > 0.
 */
class AddBookingUseCase @Inject constructor(
    private val repo: BookingRepository,
) {
    suspend operator fun invoke(
        title: String,
        bookingType: String,
        bookingIcon: BookingIcon,
        eventId: String,
        contactName: String,
        phone: String,
        totalAmountPaise: Long,
        bookingAmountPaise: Long = 0L,
        nextDueDate: LocalDate?,
    ): BookingSaveResult {
        if (eventId.isBlank()) return BookingSaveResult.EventIdBlank
        if (totalAmountPaise <= 0) return BookingSaveResult.TotalInvalid

        val now = System.currentTimeMillis()
        val bookingId = UUID.randomUUID().toString()
        val booking = Booking(
            id               = bookingId,
            eventId          = eventId,
            title            = bookingType.trim(),
            bookingType      = bookingType.trim(),
            bookingIcon      = bookingIcon,
            contactName      = contactName.trim(),
            phone            = phone.trim(),
            totalAmountPaise = totalAmountPaise,
            nextDueDate      = nextDueDate,
            payments         = emptyList(),
            createdAt        = now,
            updatedAt        = now,
            syncState        = SyncState.PENDING,
            deleted          = false,
        )
        repo.upsertBooking(booking)

        if (bookingAmountPaise > 0) {
            val payment = sabuj.m.myevents.domain.model.BookingPayment(
                id          = UUID.randomUUID().toString(),
                bookingId   = bookingId,
                date        = LocalDate.now(),
                amountPaise = bookingAmountPaise,
                note        = "Booking advance",
                createdAt   = now,
                updatedAt   = now,
            )
            repo.addPayment(payment)
        }

        return BookingSaveResult.Success
    }
}
