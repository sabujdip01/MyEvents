package sabuj.m.myevents.domain.repository

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Gift

/** Repository interface for gifts. */
interface GiftRepository {
    fun observeAllGifts(): Flow<List<Gift>>
    fun observeGiftsForEvent(eventId: String): Flow<List<Gift>>
    suspend fun upsertGift(gift: Gift)
    suspend fun deleteGift(id: String)
}
