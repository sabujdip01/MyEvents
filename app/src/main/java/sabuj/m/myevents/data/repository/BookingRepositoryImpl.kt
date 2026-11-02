package sabuj.m.myevents.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.data.db.dao.BookingDao
import sabuj.m.myevents.data.db.dao.BookingWithPayments
import sabuj.m.myevents.data.db.entity.BookingEntity
import sabuj.m.myevents.data.db.entity.BookingPaymentEntity
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.BookingPayment
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.BookingRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [BookingRepository] backed by Room.
 * Maps between [BookingWithPayments] (data layer) and [Booking] (domain layer).
 */
@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val dao: BookingDao,
) : BookingRepository {

    override fun observeAll(): Flow<List<Booking>> =
        dao.observeAllWithPayments().map { list -> list.map { it.toDomain() } }

    override fun observeByEvent(eventId: String): Flow<List<Booking>> =
        dao.observeByEvent(eventId).map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Booking?> =
        dao.observeAllWithPayments().map { list ->
            list.find { it.booking.id == id && it.booking.deleted == 0 }?.toDomain()
        }

    override fun observeCountByEvent(eventId: String): Flow<Int> =
        dao.countByEvent(eventId)

    override suspend fun upsertBooking(booking: Booking) {
        val entity = booking.toEntity()
        val count = dao.updateBooking(entity)
        if (count == 0) {
            dao.insertBooking(entity)
        }
    }

    override suspend fun softDelete(id: String) =
        dao.softDeleteBooking(id, System.currentTimeMillis())

    override suspend fun restoreDeleted(id: String) =
        dao.restoreBooking(id, System.currentTimeMillis())

    override suspend fun hardDelete(id: String) =
        dao.hardDeleteBooking(id)

    override suspend fun addPayment(payment: BookingPayment) =
        dao.upsertPayment(payment.toEntity())

    override suspend fun deletePayment(id: String) =
        dao.deletePayment(id)

    // ── Mappers ─────────────────────────────────────────────────────────────

    private fun BookingWithPayments.toDomain() = Booking(
        id                = booking.id,
        eventId           = booking.eventId,
        title             = booking.title,
        bookingType       = booking.bookingType,
        bookingIcon       = runCatching { BookingIcon.valueOf(booking.bookingIconKey) }
                                .getOrDefault(BookingIcon.Other),
        contactName       = booking.contactName,
        phone             = booking.phone,
        totalAmountPaise  = booking.totalAmountPaise,
        nextDueDate       = booking.nextDueDateEpochDay?.let { LocalDate.ofEpochDay(it) },
        bookingMade       = booking.bookingMade == 1,
        payments          = payments.map { it.toDomain() },
        createdAt         = booking.createdAt,
        updatedAt         = booking.updatedAt,
        syncState         = runCatching { SyncState.valueOf(booking.syncState) }
                                .getOrDefault(SyncState.PENDING),
        deleted           = booking.deleted == 1,
    )

    private fun Booking.toEntity() = BookingEntity(
        id                 = id,
        eventId            = eventId,
        title              = title,
        bookingType        = bookingType,
        bookingIconKey     = bookingIcon.name,
        contactName        = contactName,
        phone              = phone,
        totalAmountPaise   = totalAmountPaise,
        nextDueDateEpochDay = nextDueDate?.toEpochDay(),
        bookingMade        = if (bookingMade) 1 else 0,
        createdAt          = createdAt,
        updatedAt          = updatedAt,
        syncState          = syncState.name,
        deleted            = if (deleted) 1 else 0,
    )

    private fun BookingPaymentEntity.toDomain() = BookingPayment(
        id          = id,
        bookingId   = bookingId,
        date        = LocalDate.ofEpochDay(dateEpochDay),
        amountPaise = amountPaise,
        note        = note,
        createdAt   = createdAt,
        updatedAt   = updatedAt,
    )

    private fun BookingPayment.toEntity() = BookingPaymentEntity(
        id          = id,
        bookingId   = bookingId,
        dateEpochDay = date.toEpochDay(),
        amountPaise = amountPaise,
        note        = note,
        createdAt   = createdAt,
        updatedAt   = updatedAt,
    )
}
