package sabuj.m.myevents.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.data.db.Converters
import sabuj.m.myevents.data.db.dao.ExpenseDao
import sabuj.m.myevents.data.db.entity.ExpenseEntity
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.ExpenseRepository
import javax.inject.Inject

/** Implementation of [ExpenseRepository] backed by Room [ExpenseDao]. */
class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao,
) : ExpenseRepository {

    private val converters = Converters()

    override fun observeAllExpenses(): Flow<List<Expense>> =
        dao.observeAllExpenses().map { list -> list.map { it.toDomain() } }

    override fun observeExpensesForEvent(eventId: String): Flow<List<Expense>> =
        dao.observeExpensesForEvent(eventId).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertExpense(expense: Expense) {
        dao.upsertExpense(expense.toEntity())
    }

    override suspend fun upsertExpenses(expenses: List<Expense>) {
        dao.upsertExpenses(expenses.map { it.toEntity() })
    }

    override suspend fun deleteExpense(id: String) {
        dao.softDeleteExpense(id, System.currentTimeMillis())
    }

    override suspend fun deleteExpenses(ids: List<String>) {
        dao.softDeleteExpenses(ids, System.currentTimeMillis())
    }

    private fun ExpenseEntity.toDomain(): Expense = Expense(
        id             = id,
        eventId        = eventId,
        groupId        = groupId,
        purpose        = purpose,
        amountPaise    = amountPaise,
        amountDuePaise = amountDuePaise,
        items          = converters.fromStringList(itemsJson),
        allReceived    = allReceived == 1,
        createdAt      = createdAt,
        updatedAt      = updatedAt,
        syncState      = try { SyncState.valueOf(syncState) } catch (e: Exception) { SyncState.PENDING },
        deleted        = deleted == 1,
    )

    private fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
        id             = id,
        eventId        = eventId,
        groupId        = groupId,
        purpose        = purpose,
        amountPaise    = amountPaise,
        amountDuePaise = amountDuePaise,
        itemsJson      = converters.toStringList(items),
        allReceived    = if (allReceived) 1 else 0,
        createdAt      = createdAt,
        updatedAt      = updatedAt,
        syncState      = syncState.name,
        deleted        = if (deleted) 1 else 0,
    )
}
