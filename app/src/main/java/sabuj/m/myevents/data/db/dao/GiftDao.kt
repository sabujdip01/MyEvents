package sabuj.m.myevents.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.data.db.entity.GiftEntity

/** Room DAO for the gifts table. */
@Dao
interface GiftDao {

    @Query("SELECT * FROM gifts WHERE deleted = 0 ORDER BY createdAt DESC")
    fun observeAllGifts(): Flow<List<GiftEntity>>

    @Query("SELECT * FROM gifts WHERE eventId = :eventId AND deleted = 0 ORDER BY createdAt DESC")
    fun observeGiftsForEvent(eventId: String): Flow<List<GiftEntity>>

    @Upsert
    suspend fun upsertGift(gift: GiftEntity)

    @Query("UPDATE gifts SET deleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteGift(id: String, updatedAt: Long)
}
