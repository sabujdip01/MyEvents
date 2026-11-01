package sabuj.m.myevents.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.data.db.dao.GiftDao
import sabuj.m.myevents.data.db.entity.GiftEntity
import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.GiftRepository
import javax.inject.Inject

class GiftRepositoryImpl @Inject constructor(
    private val dao: GiftDao,
) : GiftRepository {

    override fun observeAllGifts(): Flow<List<Gift>> =
        dao.observeAllGifts().map { list -> list.map { it.toDomain() } }

    override fun observeGiftsForEvent(eventId: String): Flow<List<Gift>> =
        dao.observeGiftsForEvent(eventId).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertGift(gift: Gift) {
        dao.upsertGift(gift.toEntity())
    }

    override suspend fun deleteGift(id: String) {
        dao.softDeleteGift(id, System.currentTimeMillis())
    }

    private fun GiftEntity.toDomain(): Gift = Gift(
        id          = id,
        eventId     = eventId,
        giverName   = giverName,
        amountPaise = amountPaise,
        description = description,
        inviteeId   = inviteeId,
        createdAt   = createdAt,
        updatedAt   = updatedAt,
        syncState   = try { SyncState.valueOf(syncState) } catch (e: Exception) { SyncState.PENDING },
        deleted     = deleted == 1,
    )

    private fun Gift.toEntity(): GiftEntity = GiftEntity(
        id          = id,
        eventId     = eventId,
        giverName   = giverName,
        amountPaise = amountPaise,
        description = description,
        inviteeId   = inviteeId,
        createdAt   = createdAt,
        updatedAt   = updatedAt,
        syncState   = syncState.name,
        deleted     = if (deleted) 1 else 0,
    )
}
