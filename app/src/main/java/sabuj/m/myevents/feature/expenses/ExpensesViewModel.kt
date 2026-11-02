package sabuj.m.myevents.feature.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.domain.usecase.AddExpenseUseCase
import sabuj.m.myevents.domain.usecase.DeleteExpensesUseCase
import sabuj.m.myevents.domain.usecase.EditExpenseUseCase
import sabuj.m.myevents.domain.usecase.ExpenseSaveResult
import sabuj.m.myevents.domain.usecase.ObserveBookingsUseCase
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.ObserveExpensesUseCase
import sabuj.m.myevents.domain.usecase.ObserveTasksUseCase
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
    private val observeBookings: ObserveBookingsUseCase,
    private val observeExpenses: ObserveExpensesUseCase,
    private val observeTasks: ObserveTasksUseCase,
    private val addExpense: AddExpenseUseCase,
    private val editExpense: EditExpenseUseCase,
    private val deleteExpenses: DeleteExpensesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                observeEvents(),
                observeBookings(),
                observeExpenses(),
                observeTasks(),
            ) { events, bookings, expenses, tasks ->
                _uiState.value.copy(
                    isLoading = false,
                    events    = events,
                    bookings  = bookings,
                    expenses  = expenses,
                    tasks     = tasks,
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectTab(eventId: String?) {
        _uiState.update { it.copy(selectedEventId = eventId) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilter(filter: ExpenseCategory) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun showAddSheet() {
        _uiState.update {
            it.copy(
                sheetState = ExpenseSheetState.Add(
                    eventId = "", // Item G7: Do not auto-select event name
                )
            )
        }
    }

    fun showEditSheet(expenseId: String) {
        val exp = uiState.value.expenses.find { it.id == expenseId } ?: return
        _uiState.update {
            it.copy(
                sheetState = ExpenseSheetState.Edit(
                    expenseId        = exp.id,
                    eventId          = exp.eventId,
                    purpose          = exp.purpose,
                    totalAmountPaise = exp.amountPaise,
                )
            )
        }
    }

    fun hideSheet() {
        _uiState.update { it.copy(sheetState = ExpenseSheetState.Hidden) }
    }

    fun updateAddSheetField(update: (ExpenseSheetState.Add) -> ExpenseSheetState.Add) {
        val current = _uiState.value.sheetState as? ExpenseSheetState.Add ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun updateEditSheetField(update: (ExpenseSheetState.Edit) -> ExpenseSheetState.Edit) {
        val current = _uiState.value.sheetState as? ExpenseSheetState.Edit ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun saveExpense() {
        viewModelScope.launch {
            when (val sheet = _uiState.value.sheetState) {
                is ExpenseSheetState.Add -> {
                    val result = addExpense(
                        purpose        = sheet.purpose,
                        eventIds       = listOf(sheet.eventId),
                        amountPaise    = sheet.totalAmountPaise,
                        amountDuePaise = 0L,
                        items          = emptyList(),
                        allReceived    = false,
                    )
                    handleSaveResult(result, sheet)
                }
                is ExpenseSheetState.Edit -> {
                    val existing = uiState.value.expenses.find { it.id == sheet.expenseId } ?: return@launch
                    val result = editExpense(
                        existing       = existing,
                        purpose        = sheet.purpose,
                        amountPaise    = sheet.totalAmountPaise,
                        amountDuePaise = existing.amountDuePaise,
                        items          = existing.items,
                        allReceived    = existing.allReceived,
                    )
                    handleSaveResult(result, sheet)
                }
                else -> Unit
            }
        }
    }

    private fun handleSaveResult(result: ExpenseSaveResult, sheet: ExpenseSheetState) {
        when (result) {
            ExpenseSaveResult.Success -> hideSheet()
            ExpenseSaveResult.PurposeBlank -> applySheetError(sheet, purposeError = "Enter purpose")
            ExpenseSaveResult.EventsEmpty -> applySheetError(sheet, eventsError = "Select an event")
            ExpenseSaveResult.AmountInvalid -> applySheetError(sheet, amountError = "Enter a valid amount")
        }
    }

    private fun applySheetError(
        sheet: ExpenseSheetState,
        purposeError: String? = null,
        eventsError: String? = null,
        amountError: String? = null,
    ) {
        _uiState.update {
            it.copy(
                sheetState = when (sheet) {
                    is ExpenseSheetState.Add -> sheet.copy(
                        purposeError = purposeError,
                        eventError   = eventsError,
                        amountError  = amountError,
                    )
                    is ExpenseSheetState.Edit -> sheet.copy(
                        purposeError = purposeError,
                        amountError  = amountError,
                    )
                    else -> sheet
                }
            )
        }
    }

    fun toggleDeleteMode() {
        _uiState.update {
            it.copy(
                isDeleteMode       = !it.isDeleteMode,
                selectedForDelete  = emptySet(),
            )
        }
    }

    fun toggleSelectForDelete(expenseId: String) {
        _uiState.update { state ->
            val set = state.selectedForDelete.toMutableSet()
            if (set.contains(expenseId)) set.remove(expenseId) else set.add(expenseId)
            state.copy(selectedForDelete = set)
        }
    }

    fun deleteSelectedExpenses() {
        val ids = uiState.value.selectedForDelete.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            deleteExpenses(ids)
            _uiState.update {
                it.copy(
                    isDeleteMode       = false,
                    selectedForDelete  = emptySet(),
                    snackbarMessage    = "${ids.size} expenses deleted",
                )
            }
        }
    }

    fun snackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
