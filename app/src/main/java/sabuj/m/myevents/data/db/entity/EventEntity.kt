package sabuj.m.myevents.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for the events table.
 * - [date] is stored as epoch day (Long) with a UNIQUE index (one event per date).
 * - Money ([budgetPaise]) stored as Long paise.
 * - [syncState] one of "PENDING", "SYNCED", "FAILED".
 * - [deleted] = 1 means soft-deleted; all queries filter deleted = 0.
 */
@Entity(
    tableName = "events",
    indices = [Index(value = ["date"], unique = true)],
)
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val eventName: String,
    /** Stored as epoch day via [sabuj.m.myevents.data.db.Converters]. */
    val date: Long,
    /** Maps to [sabuj.m.myevents.core.icons.EventIcon.name]. */
    val iconKey: String,
    val budgetPaise: Long,
    val inviteesRequired: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "PENDING") val syncState: String = "PENDING",
    @ColumnInfo(defaultValue = "0") val deleted: Int = 0,
)

