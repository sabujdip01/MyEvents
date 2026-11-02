package sabuj.m.myevents.feature.expenses

import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Task

enum class ExpenseCategory { All, Vendor, ItemList, Other, DuePending }

data class DisplayExpense(
    val id: String,
    val originalId: String,
    val eventId: String,
    val purpose: String,
    val totalAmountPaise: Long,
    val paidAmountPaise: Long,
    val dueAmountPaise: Long,
    val category: ExpenseCategory,
    val isReadOnly: Boolean = false,
    val statusText: String,
)

sealed class ExpenseSheetState {
    object Hidden : ExpenseSheetState()

    data class Add(
        val purpose: String = "",
        val eventId: String = "",
        val totalAmountPaise: Long = 0L,
        val purposeError: String? = null,
        val eventError: String? = null,
        val amountError: String? = null,
    ) : ExpenseSheetState()

    data class Edit(
        val expenseId: String,
        val eventId: String,
        val purpose: String,
        val totalAmountPaise: Long,
        val purposeError: String? = null,
        val amountError: String? = null,
    ) : ExpenseSheetState()
}

/** UI state for Expenses screen. */
data class ExpensesUiState(
    val isLoading: Boolean = true,
    val events: List<Event> = emptyList(),
    val selectedEventId: String? = null, // null = All
    val expenses: List<Expense> = emptyList(),
    val bookings: List<Booking> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val filter: ExpenseCategory = ExpenseCategory.All,
    val sheetState: ExpenseSheetState = ExpenseSheetState.Hidden,
    val isDeleteMode: Boolean = false,
    val selectedForDelete: Set<String> = emptySet(),
    val deleteExpenseId: String? = null,
    val snackbarMessage: String? = null,
) {
    /** Aggregated list of display expenses combining Direct Expenses, Vendor Bookings, and Item Lists. */
    val allDisplayExpenses: List<DisplayExpense>
        get() {
            val list = mutableListOf<DisplayExpense>()

            // 1. Direct Expenses
            expenses.forEach { exp ->
                list.add(
                    DisplayExpense(
                        id               = exp.id,
                        originalId       = exp.id,
                        eventId          = exp.eventId,
                        purpose          = exp.purpose,
                        totalAmountPaise = exp.amountPaise,
                        paidAmountPaise  = (exp.amountPaise - exp.amountDuePaise).coerceAtLeast(0L),
                        dueAmountPaise   = exp.amountDuePaise,
                        category         = ExpenseCategory.Other,
                        isReadOnly       = false,
                        statusText       = if (exp.amountDuePaise <= 0L) "Paid" else "Due",
                    )
                )
            }

            // 2. Vendor Bookings
            bookings.forEach { b ->
                list.add(
                    DisplayExpense(
                        id               = "vendor-${b.id}",
                        originalId       = b.id,
                        eventId          = b.eventId,
                        purpose          = "${b.title} (Vendor)",
                        totalAmountPaise = b.totalAmountPaise,
                        paidAmountPaise  = b.paidPaise,
                        dueAmountPaise   = b.amountLeftPaise,
                        category         = ExpenseCategory.Vendor,
                        isReadOnly       = true,
                        statusText       = if (b.isFullyPaid) "Paid" else "Due",
                    )
                )
            }

            // 3. Item Lists (Tasks)
            tasks.forEach { t ->
                val paidPaise = t.items.filter { it.checked }.sumOf { it.pricePaise }
                val duePaise = (t.totalAmountPaise - paidPaise).coerceAtLeast(0L)
                list.add(
                    DisplayExpense(
                        id               = "itemlist-${t.id}",
                        originalId       = t.id,
                        eventId          = t.eventId,
                        purpose          = "${t.title} (Item List)",
                        totalAmountPaise = t.totalAmountPaise,
                        paidAmountPaise  = paidPaise,
                        dueAmountPaise   = duePaise,
                        category         = ExpenseCategory.ItemList,
                        isReadOnly       = true,
                        statusText       = if (t.completed || (t.totalAmountPaise > 0 && duePaise <= 0L)) "Paid" else "Pending",
                    )
                )
            }

            return list
        }

    /** Filtered display expenses based on event tab, search query, and category filter chip. */
    val filteredDisplayExpenses: List<DisplayExpense>
        get() {
            var result = if (selectedEventId == null) allDisplayExpenses else allDisplayExpenses.filter { it.eventId == selectedEventId }

            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                result = result.filter { it.purpose.lowercase().contains(q) }
            }

            result = when (filter) {
                ExpenseCategory.All -> result
                ExpenseCategory.Vendor -> result.filter { it.category == ExpenseCategory.Vendor }
                ExpenseCategory.ItemList -> result.filter { it.category == ExpenseCategory.ItemList }
                ExpenseCategory.Other -> result.filter { it.category == ExpenseCategory.Other }
                ExpenseCategory.DuePending -> result.filter { it.dueAmountPaise > 0L }
            }

            return result
        }
}
