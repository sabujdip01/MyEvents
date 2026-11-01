package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.repository.ExpenseRepository
import javax.inject.Inject

/** Use case to soft delete one or many expenses. */
class DeleteExpensesUseCase @Inject constructor(
    private val repo: ExpenseRepository,
) {
    suspend operator fun invoke(id: String) {
        repo.deleteExpense(id)
    }

    suspend operator fun invoke(ids: List<String>) {
        if (ids.isNotEmpty()) {
            repo.deleteExpenses(ids)
        }
    }
}
