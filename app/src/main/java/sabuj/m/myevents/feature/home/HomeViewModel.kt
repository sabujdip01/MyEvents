package sabuj.m.myevents.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sabuj.m.myevents.domain.usecase.ObserveHomeSummaryUseCase
import javax.inject.Inject

/**
 * Home screen ViewModel.
 * Collects from [ObserveHomeSummaryUseCase] and updates the state with live data.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeHomeSummary: ObserveHomeSummaryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeHomeSummary().collect { summary ->
                _state.value = HomeUiState(
                    upcomingEvents     = summary.events,
                    bookingsDone       = summary.bookingsDone,
                    bookingsTotal      = summary.bookingsTotal,
                    bookingAmountPaid  = summary.bookingAmountPaid,
                    bookingAmountTotal = summary.bookingAmountTotal,
                    budgetSpentPaise   = summary.totalSpentPaise,
                    budgetTotalPaise   = summary.totalBudgetPaise,
                    pendingDuesPaise   = summary.totalDuesPaise,
                    totalGuests        = summary.totalGuests,
                    invitedGuests      = summary.invitedGuests,
                    totalFamilies      = summary.totalFamilies,
                    invitedFamilies    = summary.invitedFamilies,
                    invitationCards    = summary.invitationCards,
                    itemsPending       = summary.itemsPending,
                )
            }
        }
    }
}
