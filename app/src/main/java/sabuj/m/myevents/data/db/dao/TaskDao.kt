package sabuj.m.myevents.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.data.db.entity.TaskEntity

/** Room DAO for the tasks table. */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE deleted = 0 ORDER BY completed ASC, createdAt DESC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE eventId = :eventId AND deleted = 0 ORDER BY completed ASC, createdAt DESC")
    fun observeTasksForEvent(eventId: String): Flow<List<TaskEntity>>

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Query("UPDATE tasks SET deleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteTask(id: String, updatedAt: Long)

    @Query("UPDATE tasks SET deleted = 1, updatedAt = :updatedAt WHERE eventId = :eventId")
    suspend fun softDeleteByEvent(eventId: String, updatedAt: Long)

    @Query("UPDATE tasks SET deleted = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun restoreTask(id: String, updatedAt: Long)

    @Query("UPDATE tasks SET deleted = 0, updatedAt = :updatedAt WHERE eventId = :eventId")
    suspend fun restoreByEvent(eventId: String, updatedAt: Long)

    @Query("DELETE FROM tasks WHERE eventId = :eventId")
    suspend fun hardDeleteByEvent(eventId: String)

    @Query("UPDATE tasks SET deleted = 1, updatedAt = :updatedAt WHERE id IN (:ids)")
    suspend fun softDeleteTasks(ids: List<String>, updatedAt: Long)
}
