package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.core.icons.BookingIcon
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
        icon: BookingIcon = existing.icon,
        dueDate: LocalDate? = existing.dueDate,
        totalAmountPaise: Long = existing.totalAmountPaise,
        items: List<sabuj.m.myevents.domain.model.TaskItem> = existing.items,
        completed: Boolean = existing.completed,
    ): TaskSaveResult {
        if (title.isBlank()) return TaskSaveResult.TitleBlank

        val updated = existing.copy(
            title            = title.trim(),
            icon             = icon,
            dueDate          = dueDate,
            totalAmountPaise = totalAmountPaise,
            items            = items,
            completed        = completed,
            updatedAt        = System.currentTimeMillis(),
        )

        repo.upsertTask(updated)
        return TaskSaveResult.Success
    }
}
