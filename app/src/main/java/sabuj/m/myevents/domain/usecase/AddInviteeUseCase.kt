package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.InviteeRepository
import java.util.UUID
import javax.inject.Inject

sealed class InviteeSaveResult {
    object Success : InviteeSaveResult()
    object NameBlank : InviteeSaveResult()
    object EventsEmpty : InviteeSaveResult()
}

/** Use case to add invitees across one or multiple events. */
class AddInviteeUseCase @Inject constructor(
    private val repo: InviteeRepository,
) {
    suspend operator fun invoke(
        name: String,
        eventIds: List<String>,
        expectedCount: Int,
        address: String,
        invited: Boolean,
        note: String,
    ): InviteeSaveResult {
        if (name.isBlank()) return InviteeSaveResult.NameBlank
        if (eventIds.isEmpty()) return InviteeSaveResult.EventsEmpty

        val now = System.currentTimeMillis()
        val groupId = UUID.randomUUID().toString()

        val list = eventIds.map { eventId ->
            Invitee(
                id            = UUID.randomUUID().toString(),
                eventId       = eventId,
                groupId       = groupId,
                name          = name.trim(),
                expectedCount = expectedCount.coerceAtLeast(1),
                address       = address.trim(),
                invited       = invited,
                note          = note.trim(),
                createdAt     = now,
                updatedAt     = now,
                syncState     = SyncState.PENDING,
                deleted       = false,
            )
        }

        repo.upsertInvitees(list)
        return InviteeSaveResult.Success
    }
}
