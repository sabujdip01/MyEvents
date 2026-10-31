package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * Soft-delete a booking (reversible within the undo window).
 * Hard-delete is called after the undo window expires.
 */
class DeleteBookingUseCase @Inject constructor(
    private val repo: BookingRepository,
) {
    /** Soft-delete: marks deleted = 1; reversible. */
    suspend fun softDelete(id: String) = repo.softDelete(id)

    /** Hard-delete: permanently removes the row. */
    suspend fun hardDelete(id: String) = repo.hardDelete(id)
}
