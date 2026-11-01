package sabuj.m.myevents.domain.repository

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Invitee

/** Repository interface for invitees / guest list. */
interface InviteeRepository {
    fun observeAllInvitees(): Flow<List<Invitee>>
    fun observeInviteesForEvent(eventId: String): Flow<List<Invitee>>
    suspend fun upsertInvitee(invitee: Invitee)
    suspend fun upsertInvitees(invitees: List<Invitee>)
    suspend fun deleteInvitee(id: String)
    suspend fun deleteInvitees(ids: List<String>)
}
