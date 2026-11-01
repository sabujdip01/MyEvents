package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.repository.TaskRepository
import javax.inject.Inject

/** Use case to soft delete checklist tasks. */
class DeleteTasksUseCase @Inject constructor(
    private val repo: TaskRepository,
) {
    suspend operator fun invoke(id: String) {
        repo.deleteTask(id)
    }

    suspend operator fun invoke(ids: List<String>) {
        if (ids.isNotEmpty()) {
            repo.deleteTasks(ids)
        }
    }
}
