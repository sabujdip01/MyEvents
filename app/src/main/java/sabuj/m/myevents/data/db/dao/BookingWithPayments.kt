package sabuj.m.myevents.data.db.dao

import androidx.room.Embedded
import androidx.room.Relation
import sabuj.m.myevents.data.db.entity.BookingEntity
import sabuj.m.myevents.data.db.entity.BookingPaymentEntity

/**
 * Room relation holder: a [BookingEntity] together with all its [BookingPaymentEntity] rows.
 * Used by [BookingDao] queries annotated with [@Transaction][androidx.room.Transaction].
 */
data class BookingWithPayments(
    @Embedded val booking: BookingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "bookingId",
    )
    val payments: List<BookingPaymentEntity>,
)
