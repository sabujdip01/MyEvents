package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.BookingRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Validates updated fields and persists an existing booking.
 * Preserves [id], [createdAt] and [payments] from the existing booking.
 */
class EditBookingUseCase @Inject constructor(
    private val repo: BookingRepository,
) {
    suspend operator fun invoke(
        existing: Booking,
        title: String,
        bookingType: String,
        bookingIcon: BookingIcon,
        eventId: String,
        contactName: String,
        phone: String,
        totalAmountPaise: Long,
        nextDueDate: LocalDate?,
        bookingMade: Boolean = true,
    ): BookingSaveResult {
        if (eventId.isBlank()) return BookingSaveResult.EventIdBlank
        if (totalAmountPaise <= 0) return BookingSaveResult.TotalInvalid

        val updated = existing.copy(
            title            = bookingType.trim(),
            bookingType      = bookingType.trim(),
            bookingIcon      = bookingIcon,
            eventId          = eventId,
            contactName      = contactName.trim(),
            phone            = phone.trim(),
            totalAmountPaise = totalAmountPaise,
            nextDueDate      = nextDueDate,
            bookingMade      = bookingMade,
            updatedAt        = System.currentTimeMillis(),
            syncState        = SyncState.PENDING,
        )
        repo.upsertBooking(updated)
        return BookingSaveResult.Success
    }
}
