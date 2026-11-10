package sabuj.m.myevents.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for invitees / guest list.
 * - [eventId] FK -> events(id) ON DELETE CASCADE.
 */
@Entity(
    tableName = "invitees",
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
        Index(value = ["groupId"]),
    ],
)
data class InviteeEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val groupId: String,
    val name: String,
    val expectedCount: Int,
    val address: String,
    val invited: Int = 0,
    val note: String = "",
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "PENDING") val syncState: String = "PENDING",
    @ColumnInfo(defaultValue = "0") val deleted: Int = 0,
    /** Invitation delivery type: "None", "Call", or "Card". */
    @ColumnInfo(defaultValue = "None") val invitationType: String = "None",
)
