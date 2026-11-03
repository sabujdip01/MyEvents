package sabuj.m.myevents.data.remote

import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.BookingPayment
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.Task

/**
 * Mapping functions between domain models and Firestore document maps.
 * All serialization logic for remote data lives here; nothing else should
 * convert domain ↔ map directly.
 */
object RemoteMappers {

    // ---------------------------------------------------------------------------
    // Event
    // ---------------------------------------------------------------------------

    /** Serialise [Event] to a Firestore-safe map. */
    fun Event.toMap(): Map<String, Any?> = mapOf(
        "id"               to id,
        "title"            to title,
        "eventName"        to eventName,
        "date"             to date.toString(),
        "icon"             to icon.name,
        "budgetPaise"      to budgetPaise,
        "inviteesRequired" to inviteesRequired,
        "createdAt"        to createdAt,
        "updatedAt"        to updatedAt,
        "syncState"        to syncState.name,
        "deleted"          to deleted,
    )

    /** Deserialize a Firestore map to [Event]. Returns null if required fields are missing. */
    fun Map<String, Any?>.toEvent(): Event? {
        val id        = this["id"]        as? String ?: return null
        val title     = this["title"]     as? String ?: return null
        val eventName = (this["eventName"] as? String) ?: title
        val dateStr   = (this["date"]      as? String)
        val date      = dateStr?.let { runCatching { java.time.LocalDate.parse(it) }.getOrNull() } ?: java.time.LocalDate.now()
        val iconName  = (this["icon"]      as? String) ?: sabuj.m.myevents.core.icons.EventIcon.Other.name
        val icon      = runCatching { sabuj.m.myevents.core.icons.EventIcon.valueOf(iconName) }.getOrDefault(sabuj.m.myevents.core.icons.EventIcon.Other)
        val syncName  = (this["syncState"] as? String) ?: sabuj.m.myevents.domain.model.SyncState.SYNCED.name
        val syncState = runCatching { sabuj.m.myevents.domain.model.SyncState.valueOf(syncName) }.getOrDefault(sabuj.m.myevents.domain.model.SyncState.SYNCED)

        return Event(
            id               = id,
            title            = title,
            eventName        = eventName,
            date             = date,
            icon             = icon,
            budgetPaise      = (this["budgetPaise"]      as? Long) ?: 0L,
            inviteesRequired = (this["inviteesRequired"] as? Boolean) ?: false,
            createdAt        = (this["createdAt"]        as? Long) ?: System.currentTimeMillis(),
            updatedAt        = (this["updatedAt"]        as? Long) ?: System.currentTimeMillis(),
            syncState        = syncState,
            deleted          = (this["deleted"]          as? Boolean) ?: false,
        )
    }

    // TODO: add toMap / toXxx for Booking, Expense, Invitee, Task, Gift, BookingPayment
}

