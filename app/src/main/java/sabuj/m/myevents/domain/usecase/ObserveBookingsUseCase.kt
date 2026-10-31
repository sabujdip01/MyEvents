package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Observe all live bookings, or bookings for a specific event.
 */
class ObserveBookingsUseCase @Inject constructor(
    private val repo: BookingRepository,
) {
    /** Observe all non-deleted bookings. */
    operator fun invoke(): Flow<List<Booking>> = repo.observeAll()

    /** Observe all non-deleted bookings for a specific [eventId]. */
    fun byEvent(eventId: String): Flow<List<Booking>> = repo.observeByEvent(eventId)
}
