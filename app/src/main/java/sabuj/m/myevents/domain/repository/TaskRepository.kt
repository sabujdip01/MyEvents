package sabuj.m.myevents.domain.repository

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Task

/** Repository interface for checklist tasks. */
interface TaskRepository {
    fun observeAllTasks(): Flow<List<Task>>
    fun observeTasksForEvent(eventId: String): Flow<List<Task>>
    suspend fun upsertTask(task: Task)
    suspend fun deleteTask(id: String)
    suspend fun deleteTasks(ids: List<String>)
}
