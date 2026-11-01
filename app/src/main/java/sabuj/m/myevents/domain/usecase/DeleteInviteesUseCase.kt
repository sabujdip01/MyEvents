package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.repository.InviteeRepository
import javax.inject.Inject

/** Use case to soft delete one or many invitees. */
class DeleteInviteesUseCase @Inject constructor(
    private val repo: InviteeRepository,
) {
    suspend operator fun invoke(id: String) {
        repo.deleteInvitee(id)
    }

    suspend operator fun invoke(ids: List<String>) {
        if (ids.isNotEmpty()) {
            repo.deleteInvitees(ids)
        }
    }
}
