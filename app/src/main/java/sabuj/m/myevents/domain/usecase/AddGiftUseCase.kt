package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.GiftRepository
import java.util.UUID
import javax.inject.Inject

sealed class GiftSaveResult {
    object Success : GiftSaveResult()
    object GiverBlank : GiftSaveResult()
    object EventBlank : GiftSaveResult()
}

/** Use case to add a gift record. */
class AddGiftUseCase @Inject constructor(
    private val repo: GiftRepository,
) {
    suspend operator fun invoke(
        giverName: String,
        eventId: String,
        amountPaise: Long,
        description: String,
        inviteeId: String?,
    ): GiftSaveResult {
        if (giverName.isBlank()) return GiftSaveResult.GiverBlank
        if (eventId.isBlank()) return GiftSaveResult.EventBlank

        val now = System.currentTimeMillis()
        val gift = Gift(
            id          = UUID.randomUUID().toString(),
            eventId     = eventId,
            giverName   = giverName.trim(),
            amountPaise = amountPaise,
            description = description.trim(),
            inviteeId   = inviteeId,
            createdAt   = now,
            updatedAt   = now,
            syncState   = SyncState.PENDING,
            deleted     = false,
        )

        repo.upsertGift(gift)
        return GiftSaveResult.Success
    }
}
