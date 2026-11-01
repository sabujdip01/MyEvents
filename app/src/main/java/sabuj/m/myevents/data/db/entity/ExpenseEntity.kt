package sabuj.m.myevents.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for expenses table.
 * - [eventId] FK -> events(id) ON DELETE CASCADE.
 * - [groupId] shared ID if created for multiple events simultaneously.
 * - [itemsJson] delimited string converted via Converters.
 */
@Entity(
    tableName = "expenses",
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
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val groupId: String,
    val purpose: String,
    val amountPaise: Long,
    val amountDuePaise: Long,
    val itemsJson: String,
    val allReceived: Int = 0,
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "PENDING") val syncState: String = "PENDING",
    @ColumnInfo(defaultValue = "0") val deleted: Int = 0,
)
