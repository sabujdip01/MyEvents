package sabuj.m.myevents.domain.repository

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate

/**
 * Contract for event persistence.
 * Implementations live in data/repository/.
 * Queries always filter soft-deleted rows (deleted = false).
 */
interface EventRepository {

    /** Observe all live (non-deleted) events as a Flow. */
    fun observeAll(): Flow<List<Event>>

    /** Observe a single event by [id]. Emits null if not found or soft-deleted. */
    fun observeById(id: String): Flow<Event?>

    /** Insert or replace an event. Sets syncState = PENDING and updatedAt. */
    suspend fun upsert(event: Event)

    /**
     * Soft-delete an event (and its children via CASCADE soft-delete logic in the impl).
     * The caller shows an Undo snackbar; call [restoreDeleted] within the window,
     * or [hardDelete] when the window expires.
     */
    suspend fun softDelete(id: String)

    /** Restore a previously soft-deleted event. */
    suspend fun restoreDeleted(id: String)

    /** Permanently remove the event row (called after Undo window expires). */
    suspend fun hardDelete(id: String)

    /** True if [date] is already taken by another event. */
    suspend fun isDateTaken(date: LocalDate, excludeId: String? = null): Boolean
}

