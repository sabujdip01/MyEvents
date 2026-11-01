package sabuj.m.myevents.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.data.db.Converters
import sabuj.m.myevents.data.db.dao.TaskDao
import sabuj.m.myevents.data.db.entity.TaskEntity
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao,
) : TaskRepository {

    private val converters = Converters()

    override fun observeAllTasks(): Flow<List<Task>> =
        dao.observeAllTasks().map { list -> list.map { it.toDomain() } }

    override fun observeTasksForEvent(eventId: String): Flow<List<Task>> =
        dao.observeTasksForEvent(eventId).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertTask(task: Task) {
        dao.upsertTask(task.toEntity())
    }

    override suspend fun deleteTask(id: String) {
        dao.softDeleteTask(id, System.currentTimeMillis())
    }

    override suspend fun deleteTasks(ids: List<String>) {
        dao.softDeleteTasks(ids, System.currentTimeMillis())
    }

    private fun TaskEntity.toDomain(): Task = Task(
        id          = id,
        eventId     = eventId,
        title       = title,
        dueDate     = dueDateEpochDay?.let { converters.fromEpochDay(it) },
        completed   = completed == 1,
        createdAt   = createdAt,
        updatedAt   = updatedAt,
        syncState   = try { SyncState.valueOf(syncState) } catch (e: Exception) { SyncState.PENDING },
        deleted     = deleted == 1,
    )

    private fun Task.toEntity(): TaskEntity = TaskEntity(
        id              = id,
        eventId         = eventId,
        title           = title,
        dueDateEpochDay = dueDate?.let { converters.toEpochDay(it) },
        completed       = if (completed) 1 else 0,
        createdAt       = createdAt,
        updatedAt       = updatedAt,
        syncState       = syncState.name,
        deleted         = if (deleted) 1 else 0,
    )
}
