package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.ExpenseRepository
import java.util.UUID
import javax.inject.Inject

sealed class ExpenseSaveResult {
    object Success : ExpenseSaveResult()
    object PurposeBlank : ExpenseSaveResult()
    object EventsEmpty : ExpenseSaveResult()
    object AmountInvalid : ExpenseSaveResult()
}

/**
 * Use case to add an expense.
 * Supports multi-event selection: creates one expense row per event sharing a [groupId].
 */
class AddExpenseUseCase @Inject constructor(
    private val repo: ExpenseRepository,
) {
    suspend operator fun invoke(
        purpose: String,
        eventIds: List<String>,
        amountPaise: Long,
        amountDuePaise: Long,
        items: List<String>,
        allReceived: Boolean,
    ): ExpenseSaveResult {
        if (purpose.isBlank()) return ExpenseSaveResult.PurposeBlank
        if (eventIds.isEmpty()) return ExpenseSaveResult.EventsEmpty
        if (amountPaise <= 0) return ExpenseSaveResult.AmountInvalid

        val now = System.currentTimeMillis()
        val groupId = UUID.randomUUID().toString()

        val expenses = eventIds.map { eventId ->
            Expense(
                id             = UUID.randomUUID().toString(),
                eventId        = eventId,
                groupId        = groupId,
                purpose        = purpose.trim(),
                amountPaise    = amountPaise,
                amountDuePaise = amountDuePaise.coerceAtMost(amountPaise),
                items          = items.filter { it.isNotBlank() },
                allReceived    = allReceived,
                createdAt      = now,
                updatedAt      = now,
                syncState      = SyncState.PENDING,
                deleted        = false,
            )
        }

        repo.upsertExpenses(expenses)
        return ExpenseSaveResult.Success
    }
}
