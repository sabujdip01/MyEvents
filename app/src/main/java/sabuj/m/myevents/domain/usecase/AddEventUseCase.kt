package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.EventRepository
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/** Result type for add/edit operations that may fail validation. */
sealed class EventSaveResult {
    data object Success : EventSaveResult()
    data object DateTaken : EventSaveResult()
}

/**
 * Add a new event after validating that the date is not already taken.
 * Generates a UUID for the new event.
 */
class AddEventUseCase @Inject constructor(
    private val repo: EventRepository,
) {
    suspend operator fun invoke(
        title: String,
        eventName: String,
        date: LocalDate,
        icon: EventIcon,
        budgetPaise: Long,
        inviteesRequired: Boolean,
    ): EventSaveResult {
        if (repo.isDateTaken(date)) return EventSaveResult.DateTaken
        val now = System.currentTimeMillis()
        val cleanTitle = title.trim()
        val cleanEventName = eventName.trim()
        repo.upsert(
            Event(
                id               = UUID.randomUUID().toString(),
                title            = cleanTitle,
                eventName        = cleanEventName,
                date             = date,
                icon             = icon,
                budgetPaise      = budgetPaise,
                inviteesRequired = inviteesRequired,
                createdAt        = now,
                updatedAt        = now,
                syncState        = SyncState.PENDING,
            )
        )
        return EventSaveResult.Success
    }
}

