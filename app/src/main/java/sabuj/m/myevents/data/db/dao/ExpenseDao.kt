package sabuj.m.myevents.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.data.db.entity.ExpenseEntity

/** Room DAO for the expenses table. */
@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE deleted = 0 ORDER BY createdAt DESC")
    fun observeAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE eventId = :eventId AND deleted = 0 ORDER BY createdAt DESC")
    fun observeExpensesForEvent(eventId: String): Flow<List<ExpenseEntity>>

    @Upsert
    suspend fun upsertExpense(expense: ExpenseEntity)

    @Upsert
    suspend fun upsertExpenses(expenses: List<ExpenseEntity>)

    @Query("UPDATE expenses SET deleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteExpense(id: String, updatedAt: Long)

    @Query("UPDATE expenses SET deleted = 1, updatedAt = :updatedAt WHERE eventId = :eventId")
    suspend fun softDeleteByEvent(eventId: String, updatedAt: Long)

    @Query("UPDATE expenses SET deleted = 0, updatedAt = :updatedAt WHERE eventId = :eventId")
    suspend fun restoreByEvent(eventId: String, updatedAt: Long)

    @Query("DELETE FROM expenses WHERE eventId = :eventId")
    suspend fun hardDeleteByEvent(eventId: String)

    @Query("UPDATE expenses SET deleted = 1, updatedAt = :updatedAt WHERE id IN (:ids)")
    suspend fun softDeleteExpenses(ids: List<String>, updatedAt: Long)
}
