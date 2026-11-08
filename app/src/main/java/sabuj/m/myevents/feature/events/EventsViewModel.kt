package sabuj.m.myevents.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.usecase.AddEventUseCase
import sabuj.m.myevents.domain.usecase.EditEventUseCase
import sabuj.m.myevents.domain.usecase.EventSaveResult
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.RestoreEventUseCase
import sabuj.m.myevents.domain.usecase.SoftDeleteEventUseCase
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.InviteeRepository
import java.time.YearMonth
import javax.inject.Inject

private data class EventsStateTuple(
    val events: List<Event>,
    val month: YearMonth,
    val sheet: SheetMode,
    val error: String?,
)

private data class ExtraStateTuple(
    val snackbar: String?,
    val bookings: List<Booking>,
    val invitees: List<Invitee>,
)

/**
 * ViewModel for [EventsScreen].
 * Exposes [uiState] as a StateFlow and accepts user intents via [onEvent].
 * Date-conflict errors are surfaced inline via [EventsUiState.errorMessage].
 */
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
    private val addEvent: AddEventUseCase,
    private val editEvent: EditEventUseCase,
    private val softDeleteEvent: SoftDeleteEventUseCase,
    private val restoreEvent: RestoreEventUseCase,
    private val bookingRepository: BookingRepository,
    private val inviteeRepository: InviteeRepository,
) : ViewModel() {

    private val _month = MutableStateFlow(YearMonth.now())
    private val _sheet = MutableStateFlow<SheetMode>(SheetMode.None)
    private val _error = MutableStateFlow<String?>(null)
    private val _snackbar = MutableStateFlow<String?>(null)
    private val _lastDeletedId = MutableStateFlow<String?>(null)

    val uiState = combine(
        combine(observeEvents(), _month, _sheet, _error, ::EventsStateTuple),
        combine(_snackbar, bookingRepository.observeAll(), inviteeRepository.observeAllInvitees(), ::ExtraStateTuple),
    ) { main, extra ->
        val events = main.events
        val month = main.month
        val sheet = main.sheet
        val error = main.error
        val snackbar = extra.snackbar
        val bookings = extra.bookings
        val invitees = extra.invitees

        val selectedEvent = (sheet as? SheetMode.Details)?.event
        val bCount = if (selectedEvent != null) bookings.count { it.eventId == selectedEvent.id } else 0
        val iCount = if (selectedEvent != null) {
            invitees.filter { it.eventId == selectedEvent.id }.sumOf { maxOf(1, it.expectedCount) }
        } else 0

        EventsUiState(
            currentMonth       = month,
            events             = events,
            eventsByDate       = events.associateBy { it.date.toEpochDay() },
            selectedDate       = selectedEvent?.date
                ?: (sheet as? SheetMode.Add)?.date
                ?: (sheet as? SheetMode.Edit)?.event?.date,
            sheetMode          = sheet,
            bookingCount       = bCount,
            inviteeCount       = iCount,
            isLoading          = false,
            errorMessage       = error,
            snackbarMessage    = snackbar,
            lastDeletedEventId = _lastDeletedId.value,
        )
    }.stateIn(
        scope            = viewModelScope,
        started          = SharingStarted.WhileSubscribed(5_000),
        initialValue     = EventsUiState(),
    )

    fun onEvent(event: EventsUiEvent) {
        when (event) {
            is EventsUiEvent.MonthChanged      -> _month.value = event.month
            is EventsUiEvent.DateTapped        -> handleDateTap(event)
            is EventsUiEvent.DateLongPressed   -> handleLongPress(event)
            // Clear error when sheet is dismissed so stale message doesn't show on reopen
            is EventsUiEvent.SheetDismissed    -> { _sheet.value = SheetMode.None; _error.value = null }
            is EventsUiEvent.EditRequested     -> _sheet.value = SheetMode.Edit(event.event)
            is EventsUiEvent.DeleteRequested   -> _sheet.value = SheetMode.ConfirmDelete(event.event)
            is EventsUiEvent.DeleteConfirmed   -> confirmDelete()
            is EventsUiEvent.UndoDelete        -> undoDelete()
            is EventsUiEvent.SnackbarDismissed -> _snackbar.value = null
            is EventsUiEvent.SaveNew           -> saveNew(event)
            is EventsUiEvent.SaveEdit          -> saveEdit(event)
        }
    }

    private fun handleDateTap(event: EventsUiEvent.DateTapped) {
        val existing = uiState.value.eventsByDate[event.date.toEpochDay()]
        if (existing != null) _sheet.value = SheetMode.Details(existing)
        // Tap on empty date does nothing (per PRD)
    }

    private fun handleLongPress(event: EventsUiEvent.DateLongPressed) {
        val existing = uiState.value.eventsByDate[event.date.toEpochDay()]
        _sheet.value = if (existing != null) SheetMode.Edit(existing)
        else SheetMode.Add(event.date)
    }

    /** Soft-delete event with Undo support. */
    private fun confirmDelete() {
        val mode = uiState.value.sheetMode as? SheetMode.ConfirmDelete ?: return
        val eventToDelete = mode.event
        _sheet.value = SheetMode.None
        _lastDeletedId.value = eventToDelete.id
        _snackbar.value = "\"${eventToDelete.title}\" deleted"
        viewModelScope.launch { softDeleteEvent(eventToDelete.id) }
    }

    private fun undoDelete() {
        val id = _lastDeletedId.value ?: return
        _lastDeletedId.value = null
        viewModelScope.launch { restoreEvent(id) }
    }

    private fun saveNew(ev: EventsUiEvent.SaveNew) {
        viewModelScope.launch {
            val result = addEvent(ev.title, ev.eventName, ev.date, ev.icon, ev.budgetPaise, ev.inviteesRequired)
            if (result is EventSaveResult.DateTaken) {
                // Set inline error — visible inside the open sheet
                _error.value = "An event is already planned for that date."
            } else {
                _error.value = null
                _sheet.value = SheetMode.None
            }
        }
    }

    private fun saveEdit(ev: EventsUiEvent.SaveEdit) {
        val existing = (uiState.value.sheetMode as? SheetMode.Edit)?.event ?: return
        viewModelScope.launch {
            val result = editEvent(existing, ev.title, ev.eventName, ev.date, ev.icon, ev.budgetPaise, ev.inviteesRequired)
            if (result is EventSaveResult.DateTaken) {
                _error.value = "An event is already planned for that date."
            } else {
                _error.value = null
                _sheet.value = SheetMode.None
            }
        }
    }
}