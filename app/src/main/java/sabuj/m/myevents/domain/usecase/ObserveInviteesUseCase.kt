package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.repository.InviteeRepository
import javax.inject.Inject

/** Use case to observe invitees. */
class ObserveInviteesUseCase @Inject constructor(
    private val repo: InviteeRepository,
) {
    operator fun invoke(eventId: String? = null): Flow<List<Invitee>> {
        return if (eventId == null) repo.observeAllInvitees()
        else repo.observeInviteesForEvent(eventId)
    }
}
