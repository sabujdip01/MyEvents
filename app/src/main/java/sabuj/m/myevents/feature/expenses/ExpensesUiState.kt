package sabuj.m.myevents.feature.expenses

import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Expense

enum class ExpenseFilter { All, DuePending, ItemsPending }

sealed class ExpenseSheetState {
    object Hidden : ExpenseSheetState()

    data class Add(
        val purpose: String = "",
        val selectedEventIds: Set<String> = emptySet(),
        val totalAmountPaise: Long = 0L,
        val amountDuePaise: Long = 0L,
        val items: List<String> = listOf(""), // initial blank box
        val allReceived: Boolean = false,
        val purposeError: String? = null,
        val eventsError: String? = null,
        val amountError: String? = null,
    ) : ExpenseSheetState()

    data class Edit(
        val expenseId: String,
        val eventId: String,
        val purpose: String,
        val totalAmountPaise: Long,
        val amountDuePaise: Long,
        val items: List<String>,
        val allReceived: Boolean,
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
    val searchQuery: String = "",
    val filter: ExpenseFilter = ExpenseFilter.All,
    val sheetState: ExpenseSheetState = ExpenseSheetState.Hidden,
    val isDeleteMode: Boolean = false,
    val selectedForDelete: Set<String> = emptySet(),
    val snackbarMessage: String? = null,
) {
    /** Filtered list of expenses according to selected event, search query, and filter chips. */
    val filteredExpenses: List<Expense>
        get() {
            var result = if (selectedEventId == null) expenses else expenses.filter { it.eventId == selectedEventId }

            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                result = result.filter { exp ->
                    exp.purpose.lowercase().contains(q) || exp.items.any { it.lowercase().contains(q) }
                }
            }

            result = when (filter) {
                ExpenseFilter.All -> result
                ExpenseFilter.DuePending -> result.filter { it.amountDuePaise > 0 }
                ExpenseFilter.ItemsPending -> result.filter { !it.allReceived }
            }

            return result
        }

    /** Filtered list of bookings for read-only expense rows. */
    val filteredBookings: List<Booking>
        get() = if (selectedEventId == null) bookings else bookings.filter { it.eventId == selectedEventId }
}
