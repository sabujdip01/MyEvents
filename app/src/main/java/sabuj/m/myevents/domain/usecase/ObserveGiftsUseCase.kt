package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.repository.GiftRepository
import javax.inject.Inject

/** Use case to observe gifts received. */
class ObserveGiftsUseCase @Inject constructor(
    private val repo: GiftRepository,
) {
    operator fun invoke(eventId: String? = null): Flow<List<Gift>> {
        return if (eventId == null) repo.observeAllGifts()
        else repo.observeGiftsForEvent(eventId)
    }
}
