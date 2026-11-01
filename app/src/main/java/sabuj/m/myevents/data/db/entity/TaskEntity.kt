package sabuj.m.myevents.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for checklist tasks.
 * - [eventId] FK -> events(id) ON DELETE CASCADE.
 */
@Entity(
    tableName = "tasks",
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
data class TaskEntity(
    @PrimaryKey val id: String,
    val eventId: String,
    val title: String,
    val dueDateEpochDay: Long?,
    val completed: Int = 0,
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "PENDING") val syncState: String = "PENDING",
    @ColumnInfo(defaultValue = "0") val deleted: Int = 0,
)
