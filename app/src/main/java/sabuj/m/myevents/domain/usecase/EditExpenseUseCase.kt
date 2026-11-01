package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.repository.ExpenseRepository
import javax.inject.Inject

/** Use case to update an existing expense. */
class EditExpenseUseCase @Inject constructor(
    private val repo: ExpenseRepository,
) {
    suspend operator fun invoke(
        existing: Expense,
        purpose: String,
        amountPaise: Long,
        amountDuePaise: Long,
        items: List<String>,
        allReceived: Boolean,
    ): ExpenseSaveResult {
        if (purpose.isBlank()) return ExpenseSaveResult.PurposeBlank
        if (amountPaise <= 0) return ExpenseSaveResult.AmountInvalid

        val updated = existing.copy(
            purpose        = purpose.trim(),
            amountPaise    = amountPaise,
            amountDuePaise = amountDuePaise.coerceAtMost(amountPaise),
            items          = items.filter { it.isNotBlank() },
            allReceived    = allReceived,
            updatedAt      = System.currentTimeMillis(),
        )

        repo.upsertExpense(updated)
        return ExpenseSaveResult.Success
    }
}
