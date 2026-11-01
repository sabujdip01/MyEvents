package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.repository.TaskRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

sealed class TaskSaveResult {
    object Success : TaskSaveResult()
    object TitleBlank : TaskSaveResult()
    object EventBlank : TaskSaveResult()
}

/** Use case to add a checklist task. */
class AddTaskUseCase @Inject constructor(
    private val repo: TaskRepository,
) {
    suspend operator fun invoke(
        title: String,
        eventId: String,
        dueDate: LocalDate?,
    ): TaskSaveResult {
        if (title.isBlank()) return TaskSaveResult.TitleBlank
        if (eventId.isBlank()) return TaskSaveResult.EventBlank

        val now = System.currentTimeMillis()
        val task = Task(
            id        = UUID.randomUUID().toString(),
            eventId   = eventId,
            title     = title.trim(),
            dueDate   = dueDate,
            completed = false,
            createdAt = now,
            updatedAt = now,
            syncState = SyncState.PENDING,
            deleted   = false,
        )

        repo.upsertTask(task)
        return TaskSaveResult.Success
    }
}
