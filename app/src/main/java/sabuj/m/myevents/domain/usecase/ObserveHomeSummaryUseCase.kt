package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.domain.model.EventSummary
import sabuj.m.myevents.domain.model.HomeSummary
import sabuj.m.myevents.domain.repository.EventRepository
import javax.inject.Inject

/**
 * Derives the [HomeSummary] shown on the Home dashboard.
 *
 * Phase 2 supplies event data only.
 * Budget/spent/dues/guests/bookings will be wired in later phases
 * when those repositories exist. For now those values default to 0.
 */
class ObserveHomeSummaryUseCase @Inject constructor(
    private val eventRepo: EventRepository,
) {
    operator fun invoke(): Flow<HomeSummary> =
        eventRepo.observeAll().map { events ->
            HomeSummary(
                events = events.map { e ->
                    EventSummary(
                        id    = e.id,
                        title = e.title,
                        date  = e.date,
                        icon  = e.icon,
                    )
                },
                totalBudgetPaise = events.sumOf { it.budgetPaise },
                // Spent / dues / guests / bookings filled in Phase 3-6
            )
        }
}

