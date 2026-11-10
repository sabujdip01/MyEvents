package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.InvitationType
import sabuj.m.myevents.domain.repository.InviteeRepository
import javax.inject.Inject

/** Use case to update an existing invitee entry. */
class EditInviteeUseCase @Inject constructor(
    private val repo: InviteeRepository,
) {
    suspend operator fun invoke(
        existing: Invitee,
        name: String,
        expectedCount: Int,
        address: String,
        invited: Boolean,
        note: String,
        invitationType: InvitationType = existing.invitationType,
    ): InviteeSaveResult {
        if (name.isBlank()) return InviteeSaveResult.NameBlank

        val updated = existing.copy(
            name           = name.trim(),
            expectedCount  = expectedCount.coerceAtLeast(1),
            address        = address.trim(),
            invited        = invited,
            note           = note.trim(),
            updatedAt      = System.currentTimeMillis(),
            invitationType = invitationType,
        )

        repo.upsertInvitee(updated)
        return InviteeSaveResult.Success
    }
}
