package sabuj.m.myevents.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for individual payments against a booking.
 * - [bookingId] foreign key → bookings(id) with CASCADE delete.
 * - [amountPaise] is stored in paise (Long).
 * - [dateEpochDay] is the payment date stored as epoch day.
 */
@Entity(
    tableName = "booking_payments",
    foreignKeys = [
        ForeignKey(
            entity = BookingEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookingId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["bookingId"])],
)
data class BookingPaymentEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    /** Payment date stored as epoch day. */
    val dateEpochDay: Long,
    /** Amount paid in paise. */
    val amountPaise: Long,
    val note: String = "",
    val createdAt: Long,
    val updatedAt: Long,
)
