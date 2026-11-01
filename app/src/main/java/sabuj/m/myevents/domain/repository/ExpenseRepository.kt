package sabuj.m.myevents.domain.repository

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Expense

/** Repository interface for expenses. */
interface ExpenseRepository {
    fun observeAllExpenses(): Flow<List<Expense>>
    fun observeExpensesForEvent(eventId: String): Flow<List<Expense>>
    suspend fun upsertExpense(expense: Expense)
    suspend fun upsertExpenses(expenses: List<Expense>)
    suspend fun deleteExpense(id: String)
    suspend fun deleteExpenses(ids: List<String>)
}
