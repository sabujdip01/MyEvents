package sabuj.m.myevents.feature.expenses

import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Task

enum class ExpenseCategory { All, Bookings, ItemList, Due, Paid }

data class DisplayExpense(
    val id: String,
    val originalId: String,
    val eventId: String,
    val purpose: String,
    val totalAmountPaise: Long,
    val paidAmountPaise: Long,
    val dueAmountPaise: Long,
    val category: ExpenseCategory,
    val isReadOnly: Boolean = true,
    val statusText: String,
)

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
                        category         = ExpenseCategory.ItemList,
                        isReadOnly       = true,
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
                        purpose          = "${b.title} (Booking)",
                        totalAmountPaise = b.totalAmountPaise,
                        paidAmountPaise  = b.paidPaise,
                        dueAmountPaise   = b.amountLeftPaise,
                        category         = ExpenseCategory.Bookings,
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
                        statusText       = if (duePaise <= 0L || t.completed) "Paid" else "Due",
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
                ExpenseCategory.Bookings -> result.filter { it.category == ExpenseCategory.Bookings }
                ExpenseCategory.ItemList -> result.filter { it.category == ExpenseCategory.ItemList }
                ExpenseCategory.Due -> result.filter { it.dueAmountPaise > 0L }
                ExpenseCategory.Paid -> result.filter { it.dueAmountPaise <= 0L }
            }

            return result
        }
}
