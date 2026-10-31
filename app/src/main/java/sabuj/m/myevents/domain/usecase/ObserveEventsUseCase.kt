package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.repository.EventRepository
import javax.inject.Inject

/**
 * Observe all live events as a Flow.
 * The list is ordered by date ascending (enforced in the DAO).
 */
class ObserveEventsUseCase @Inject constructor(
    private val repo: EventRepository,
) {
    operator fun invoke(): Flow<List<Event>> = repo.observeAll()
}

