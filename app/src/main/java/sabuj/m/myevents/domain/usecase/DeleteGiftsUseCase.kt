package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.repository.GiftRepository
import javax.inject.Inject

/** Use case to soft delete a gift. */
class DeleteGiftsUseCase @Inject constructor(
    private val repo: GiftRepository,
) {
    suspend operator fun invoke(id: String) {
        repo.deleteGift(id)
    }
}
