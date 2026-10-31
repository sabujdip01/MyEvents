package sabuj.m.myevents.domain.repository

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.BookingPayment

/**
 * Contract for booking persistence.
 * Implementations live in data/repository/.
 * Booking queries filter soft-deleted rows (deleted = false).
 */
interface BookingRepository {

    /** Observe all live (non-deleted) bookings with their payments. */
    fun observeAll(): Flow<List<Booking>>

    /** Observe all live bookings for a specific event. */
    fun observeByEvent(eventId: String): Flow<List<Booking>>

    /** Observe a single booking by [id]; emits null if not found or soft-deleted. */
    fun observeById(id: String): Flow<Booking?>

    /** Count non-deleted bookings for a given event — for the events detail popup. */
    fun observeCountByEvent(eventId: String): Flow<Int>

    /** Insert or update a booking. */
    suspend fun upsertBooking(booking: Booking)

    /** Soft-delete a booking (can be undone within the undo window). */
    suspend fun softDelete(id: String)

    /** Restore a previously soft-deleted booking (undo). */
    suspend fun restoreDeleted(id: String)

    /** Permanently delete a booking (called after undo window expires). */
    suspend fun hardDelete(id: String)

    /** Add or update a payment record. */
    suspend fun addPayment(payment: BookingPayment)

    /** Permanently delete a single payment by id. */
    suspend fun deletePayment(id: String)
}
