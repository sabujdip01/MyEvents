package sabuj.m.myevents.domain.model

/** Domain model for an invitee / guest family. */
data class Invitee(
    val id: String,
    val eventId: String,
    val groupId: String,
    val name: String,
    val expectedCount: Int,
    val address: String,
    val invited: Boolean,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long,
    val syncState: SyncState = SyncState.PENDING,
    val deleted: Boolean = false,
)
