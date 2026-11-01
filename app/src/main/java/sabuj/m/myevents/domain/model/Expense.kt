package sabuj.m.myevents.domain.model

/**
 * Domain model for an expense entry.
 *
 * @param id             Unique UUID string.
 * @param eventId        The owning event's ID.
 * @param groupId        Group ID if this expense was added across multiple events.
 * @param purpose        Short description e.g. "Flower decoration".
 * @param amountPaise    Total agreed/estimated expense in paise.
 * @param amountDuePaise Remaining unpaid amount in paise.
 * @param items          List of items or task items for this expense.
 * @param allReceived    True if all items have been collected/received.
 */
data class Expense(
    val id: String,
    val eventId: String,
    val groupId: String,
    val purpose: String,
    val amountPaise: Long,
    val amountDuePaise: Long,
    val items: List<String>,
    val allReceived: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: SyncState = SyncState.PENDING,
    val deleted: Boolean = false,
)
