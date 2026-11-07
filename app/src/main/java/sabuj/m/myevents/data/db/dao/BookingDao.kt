package sabuj.m.myevents.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.data.db.entity.BookingEntity
import sabuj.m.myevents.data.db.entity.BookingPaymentEntity

/**
 * Data Access Object for the bookings and booking_payments tables.
 * All booking select queries filter deleted = 0 (soft-delete pattern).
 */
@Dao
interface BookingDao {

    /** Observe all live bookings (non-deleted) with their payments. */
    @Transaction
    @Query("SELECT * FROM bookings WHERE deleted = 0 ORDER BY createdAt DESC")
    fun observeAllWithPayments(): Flow<List<BookingWithPayments>>

    /** Observe all live bookings for a specific event, with payments. */
    @Transaction
    @Query("SELECT * FROM bookings WHERE eventId = :eventId AND deleted = 0 ORDER BY createdAt DESC")
    fun observeByEvent(eventId: String): Flow<List<BookingWithPayments>>

    /** Insert or update a booking row. */
    @Upsert
    suspend fun upsertBooking(entity: BookingEntity)

    /** Soft-delete a booking (sets deleted = 1). */
    @Query("UPDATE bookings SET deleted = 1, syncState = 'PENDING', updatedAt = :now WHERE id = :id")
    suspend fun softDeleteBooking(id: String, now: Long)

    /** Restore a soft-deleted booking (sets deleted = 0). */
    @Query("UPDATE bookings SET deleted = 0, syncState = 'PENDING', updatedAt = :now WHERE id = :id")
    suspend fun restoreBooking(id: String, now: Long)

    /** Hard-delete a booking row permanently. */
    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun hardDeleteBooking(id: String)

    /** Insert or update a payment row. */
    @Upsert
    suspend fun upsertPayment(payment: BookingPaymentEntity)

    /** Hard-delete a payment row by its id. */
    @Query("DELETE FROM booking_payments WHERE id = :id")
    suspend fun deletePayment(id: String)

    /** Hard-delete all payments for a given booking id. */
    @Query("DELETE FROM booking_payments WHERE bookingId = :bookingId")
    suspend fun deletePaymentsForBooking(bookingId: String)

    /** Replace booking and all its payments in a single transaction. */
    @Transaction
    suspend fun updateBookingAndPayments(booking: BookingEntity, payments: List<BookingPaymentEntity>) {
        upsertBooking(booking)
        deletePaymentsForBooking(booking.id)
        payments.forEach { upsertPayment(it) }
    }

    /**
     * Count non-deleted bookings for a given event.
     * Used by the Events details popup bookings count badge.
     */
    @Query("SELECT COUNT(*) FROM bookings WHERE eventId = :eventId AND deleted = 0")
    fun countByEvent(eventId: String): Flow<Int>
}
