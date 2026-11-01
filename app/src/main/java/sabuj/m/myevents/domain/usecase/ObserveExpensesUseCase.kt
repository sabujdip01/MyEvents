package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.repository.ExpenseRepository
import javax.inject.Inject

/** Use case to observe expenses for all events or a specific event. */
class ObserveExpensesUseCase @Inject constructor(
    private val repo: ExpenseRepository,
) {
    operator fun invoke(eventId: String? = null): Flow<List<Expense>> {
        return if (eventId == null) repo.observeAllExpenses()
        else repo.observeExpensesForEvent(eventId)
    }
}
