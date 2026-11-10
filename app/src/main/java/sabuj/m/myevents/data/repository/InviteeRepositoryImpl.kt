package sabuj.m.myevents.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.data.db.dao.InviteeDao
import sabuj.m.myevents.data.db.entity.InviteeEntity
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.InvitationType
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.InviteeRepository
import javax.inject.Inject

class InviteeRepositoryImpl @Inject constructor(
    private val dao: InviteeDao,
) : InviteeRepository {

    override fun observeAllInvitees(): Flow<List<Invitee>> =
        dao.observeAllInvitees().map { list -> list.map { it.toDomain() } }

    override fun observeInviteesForEvent(eventId: String): Flow<List<Invitee>> =
        dao.observeInviteesForEvent(eventId).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertInvitee(invitee: Invitee) {
        dao.upsertInvitee(invitee.toEntity())
    }

    override suspend fun upsertInvitees(invitees: List<Invitee>) {
        dao.upsertInvitees(invitees.map { it.toEntity() })
    }

    override suspend fun deleteInvitee(id: String) {
        dao.softDeleteInvitee(id, System.currentTimeMillis())
    }

    override suspend fun deleteInvitees(ids: List<String>) {
        dao.softDeleteInvitees(ids, System.currentTimeMillis())
    }

    private fun InviteeEntity.toDomain(): Invitee = Invitee(
        id             = id,
        eventId        = eventId,
        groupId        = groupId,
        name           = name,
        expectedCount  = expectedCount,
        address        = address,
        invited        = invited == 1,
        note           = note,
        createdAt      = createdAt,
        updatedAt      = updatedAt,
        syncState      = try { SyncState.valueOf(syncState) } catch (e: Exception) { SyncState.PENDING },
        deleted        = deleted == 1,
        invitationType = try { InvitationType.valueOf(invitationType) } catch (e: Exception) { InvitationType.None },
    )

    private fun Invitee.toEntity(): InviteeEntity = InviteeEntity(
        id             = id,
        eventId        = eventId,
        groupId        = groupId,
        name           = name,
        expectedCount  = expectedCount,
        address        = address,
        invited        = if (invited) 1 else 0,
        note           = note,
        createdAt      = createdAt,
        updatedAt      = updatedAt,
        syncState      = syncState.name,
        deleted        = if (deleted) 1 else 0,
        invitationType = invitationType.name,
    )
}
