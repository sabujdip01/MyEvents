package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.EventRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Edit an existing event. Validates the new date is not taken by a *different* event.
 * Preserves [Event.createdAt] and bumps [Event.updatedAt].
 */
class EditEventUseCase @Inject constructor(
    private val repo: EventRepository,
) {
    suspend operator fun invoke(
        existing: Event,
        title: String,
        eventName: String,
        date: LocalDate,
        icon: EventIcon,
        budgetPaise: Long,
        inviteesRequired: Boolean,
    ): EventSaveResult {
        if (repo.isDateTaken(date, excludeId = existing.id)) return EventSaveResult.DateTaken
        val cleanTitle = title.trim()
        val cleanEventName = eventName.trim()
        repo.upsert(
            existing.copy(
                title            = cleanTitle,
                eventName        = cleanEventName,
                date             = date,
                icon             = icon,
                budgetPaise      = budgetPaise,
                inviteesRequired = inviteesRequired,
                updatedAt        = System.currentTimeMillis(),
                syncState        = SyncState.PENDING,
            )
        )
        return EventSaveResult.Success
    }
}

