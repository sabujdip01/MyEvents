package sabuj.m.myevents.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for gifts received.
 * - [eventId] FK -> events(id) ON DELETE CASCADE.
 */
@Entity(
    tableName = "gifts",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["eventId"]),
    ],
)
data class GiftEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val giverName: String,
    val amountPaise: Long,
    val description: String,
    val inviteeId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "PENDING") val syncState: String = "PENDING",
    @ColumnInfo(defaultValue = "0") val deleted: Int = 0,
)
