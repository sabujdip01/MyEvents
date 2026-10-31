package sabuj.m.myevents.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for the bookings table.
 * - [eventId] foreign key → events(id) with CASCADE delete.
 * - [bookingIconKey] stores [sabuj.m.myevents.core.icons.BookingIcon.name].
 * - [totalAmountPaise] is stored in paise (Long).
 * - [nextDueDateEpochDay] is nullable — null if no due date is set.
 * - [syncState] one of "PENDING", "SYNCED", "FAILED".
 * - [deleted] = 1 means soft-deleted; queries filter deleted = 0.
 */
@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["eventId"])],
)
data class BookingEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val title: String,
    /** Human-readable booking type/name (e.g. "Camera", "Mandap"). */
    val bookingType: String,
    /** Maps to [sabuj.m.myevents.core.icons.BookingIcon.name]. */
    val bookingIconKey: String,
    val contactName: String,
    val phone: String,
    /** Total amount for this booking in paise. */
    val totalAmountPaise: Long,
    /** Next payment due date stored as epoch day; null if unset. */
    val nextDueDateEpochDay: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "PENDING") val syncState: String = "PENDING",
    @ColumnInfo(defaultValue = "0") val deleted: Int = 0,
)
