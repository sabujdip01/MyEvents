package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.repository.TaskRepository
import javax.inject.Inject

/** Use case to observe checklist tasks. */
class ObserveTasksUseCase @Inject constructor(
    private val repo: TaskRepository,
) {
    operator fun invoke(eventId: String? = null): Flow<List<Task>> {
        return if (eventId == null) repo.observeAllTasks()
        else repo.observeTasksForEvent(eventId)
    }
}
