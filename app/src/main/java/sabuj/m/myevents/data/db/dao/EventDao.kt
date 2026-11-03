package sabuj.m.myevents.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.data.db.entity.EventEntity

/**
 * Data Access Object for the events table.
 * All select queries filter deleted = 0 (soft-delete pattern).
 */
@Dao
interface EventDao {

    /** Observe all live events ordered by date ascending. */
    @Query("SELECT * FROM events WHERE deleted = 0 ORDER BY date ASC")
    fun observeAll(): Flow<List<EventEntity>>

    /** Observe a single live event by primary key. */
    @Query("SELECT * FROM events WHERE id = :id AND deleted = 0")
    fun observeById(id: String): Flow<EventEntity?>

    /** Insert or update an event row. */
    @Upsert
    suspend fun upsert(event: EventEntity)

    /** Mark as soft-deleted and set updatedAt. */
    @Query("UPDATE events SET deleted = 1, syncState = 'PENDING', updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)

    /** Restore a soft-deleted row. */
    @Query("UPDATE events SET deleted = 0, syncState = 'PENDING', updatedAt = :now WHERE id = :id")
    suspend fun restore(id: String, now: Long)

    /** Hard-delete a row permanently. */
    @Query("DELETE FROM events WHERE id = :id")
    suspend fun hardDelete(id: String)

    /**
     * Check whether [epochDay] is already taken.
     * Optionally excludes [excludeId] (used during edits).
     */
    @Query(
        "SELECT COUNT(*) FROM events WHERE date = :epochDay AND deleted = 0 AND (:excludeId IS NULL OR id != :excludeId)"
    )
    suspend fun countByDate(epochDay: Long, excludeId: String?): Int
}
