package sabuj.m.myevents.domain.model

import sabuj.m.myevents.core.icons.EventIcon
import java.time.LocalDate

/**
 * Domain model for an event (engagement, wedding, etc.).
 * Amounts are Long paise. Dates are [LocalDate].
 */
data class Event(
    val id: String,
    val title: String,
    val eventName: String,
    /** Unique per-date constraint enforced at the DB layer. */
    val date: LocalDate,
    val icon: EventIcon,
    /** Budget in paise. */
    val budgetPaise: Long,
    /** Whether this event tracks invitees. */
    val inviteesRequired: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: SyncState = SyncState.PENDING,
    val deleted: Boolean = false,
)

