package sabuj.m.myevents.domain.model

import sabuj.m.myevents.core.icons.BookingIcon
import java.time.LocalDate

/** Item entry in an Item List. */
data class TaskItem(
    val name: String,
    val pricePaise: Long = 0L,
    val checked: Boolean = false,
)

/** Domain model for an Item List (Checklist Task). */
data class Task(
    val id: String,
    val eventId: String,
    val title: String,
    val icon: BookingIcon = BookingIcon.Cake,
    val dueDate: LocalDate?,
    val totalAmountPaise: Long = 0L,
    val items: List<TaskItem> = emptyList(),
    val completed: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: SyncState = SyncState.PENDING,
    val deleted: Boolean = false,
) {
    val totalItems: Int get() = items.count { it.name.isNotBlank() }
    val remainingItems: Int get() = items.count { it.name.isNotBlank() && !it.checked }
}
