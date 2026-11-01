package sabuj.m.myevents.domain.model

/** Domain model for a gift received. */
data class Gift(
    val id: String,
    val eventId: String,
    val giverName: String,
    val amountPaise: Long,
    val description: String,
    val inviteeId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: SyncState = SyncState.PENDING,
    val deleted: Boolean = false,
)
