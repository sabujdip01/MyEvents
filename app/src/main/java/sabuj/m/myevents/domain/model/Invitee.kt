package sabuj.m.myevents.domain.model

/**
 * How the invitation is delivered to this guest family.
 * [Card] = physical invitation card; [Call] = phone/verbal; [None] = not yet decided.
 */
enum class InvitationType { None, Call, Card }

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
    /** Delivery type for this invitation; defaults to [InvitationType.None]. */
    val invitationType: InvitationType = InvitationType.None,
)
