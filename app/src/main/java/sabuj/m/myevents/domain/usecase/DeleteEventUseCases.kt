package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.repository.EventRepository
import javax.inject.Inject

/**
 * Soft-deletes an event. The UI layer is responsible for:
 * 1. Showing a 10-second Undo snackbar.
 * 2. Calling [restoreEvent] if Undo is tapped.
 * 3. Calling [hardDeleteEvent] when the window expires.
 */
class SoftDeleteEventUseCase @Inject constructor(
    private val repo: EventRepository,
) {
    suspend operator fun invoke(id: String) = repo.softDelete(id)
}

/** Restores a soft-deleted event (called on Undo). */
class RestoreEventUseCase @Inject constructor(
    private val repo: EventRepository,
) {
    suspend operator fun invoke(id: String) = repo.restoreDeleted(id)
}

/** Permanently removes an event once the Undo window expires. */
class HardDeleteEventUseCase @Inject constructor(
    private val repo: EventRepository,
) {
    suspend operator fun invoke(id: String) = repo.hardDelete(id)
}

