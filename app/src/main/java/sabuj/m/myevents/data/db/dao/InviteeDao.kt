package sabuj.m.myevents.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.data.db.entity.InviteeEntity

/** Room DAO for the invitees table. */
@Dao
interface InviteeDao {

    @Query("SELECT * FROM invitees WHERE deleted = 0 ORDER BY name ASC")
    fun observeAllInvitees(): Flow<List<InviteeEntity>>

    @Query("SELECT * FROM invitees WHERE eventId = :eventId AND deleted = 0 ORDER BY name ASC")
    fun observeInviteesForEvent(eventId: String): Flow<List<InviteeEntity>>

    @Upsert
    suspend fun upsertInvitee(invitee: InviteeEntity)

    @Upsert
    suspend fun upsertInvitees(invitees: List<InviteeEntity>)

    @Query("UPDATE invitees SET deleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteInvitee(id: String, updatedAt: Long)

    @Query("UPDATE invitees SET deleted = 1, updatedAt = :updatedAt WHERE id IN (:ids)")
    suspend fun softDeleteInvitees(ids: List<String>, updatedAt: Long)
}
