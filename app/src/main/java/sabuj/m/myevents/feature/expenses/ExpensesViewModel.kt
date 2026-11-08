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

    fun snackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}