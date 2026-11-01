package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

/** Use case to update an existing checklist task. */
class EditTaskUseCase @Inject constructor(
    private val repo: TaskRepository,
) {
    suspend operator fun invoke(
        existing: Task,
        title: String = existing.title,
        dueDate: LocalDate? = existing.dueDate,
        completed: Boolean = existing.completed,
    ): TaskSaveResult {
        if (title.isBlank()) return TaskSaveResult.TitleBlank

        val updated = existing.copy(
            title     = title.trim(),
            dueDate   = dueDate,
            completed = completed,
            updatedAt = System.currentTimeMillis(),
        )

        repo.upsertTask(updated)
        return TaskSaveResult.Success
    }
}
