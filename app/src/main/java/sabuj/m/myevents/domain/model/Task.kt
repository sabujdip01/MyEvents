package sabuj.m.myevents.domain.model

import java.time.LocalDate

/** Domain model for a checklist task. */
data class Task(
    val id: String,
    val eventId: String,
    val title: String,
    val dueDate: LocalDate?,
    val completed: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: SyncState = SyncState.PENDING,
    val deleted: Boolean = false,
)
