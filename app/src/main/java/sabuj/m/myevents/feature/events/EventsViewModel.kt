package sabuj.m.myevents.feature.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.usecase.AddEventUseCase
import sabuj.m.myevents.domain.usecase.EditEventUseCase
import sabuj.m.myevents.domain.usecase.EventSaveResult
import sabuj.m.myevents.domain.usecase.HardDeleteEventUseCase
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.RestoreEventUseCase
import sabuj.m.myevents.domain.usecase.SoftDeleteEventUseCase
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for [EventsScreen].
 * Exposes [uiState] as a StateFlow and accepts user intents via [onEvent].
 * One-shot effects (snackbar delete-undo) are sent through [effects].
 * Date-conflict errors are surfaced inline via [EventsUiState.errorMessage].
 */
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
    private val addEvent: AddEventUseCase,
    private val editEvent: EditEventUseCase,
    private val softDelete: SoftDeleteEventUseCase,
    private val restore: RestoreEventUseCase,
    private val hardDelete: HardDeleteEventUseCase,
) : ViewModel() {

    private val _month = MutableStateFlow(YearMonth.now())
    private val _sheet = MutableStateFlow<SheetMode>(SheetMode.None)
    private val _error = MutableStateFlow<String?>(null)

    /** Pending delete awaiting Undo window expiry. */
    private var pendingDeleteId: String? = null

    val uiState = combine(
        observeEvents(),
        _month,
        _sheet,
        _error,
    ) { events, month, sheet, error ->
        EventsUiState(
            currentMonth  = month,
            events        = events,
            eventsByDate  = events.associateBy { it.date.toEpochDay() },
            selectedDate  = (sheet as? SheetMode.Details)?.event?.date
                ?: (sheet as? SheetMode.Add)?.date
                ?: (sheet as? SheetMode.Edit)?.event?.date,
            sheetMode     = sheet,
            isLoading     = false,
            errorMessage  = error,
        )
    }.stateIn(
        scope            = viewModelScope,
        started          = SharingStarted.WhileSubscribed(5_000),
        initialValue     = EventsUiState(),
    )

    private val _effects = Channel<EventsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

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

    private fun confirmDelete() {
        val mode = uiState.value.sheetMode as? SheetMode.ConfirmDelete ?: return
        val id   = mode.event.id
        _sheet.value = SheetMode.None
        viewModelScope.launch {
            softDelete(id)
            pendingDeleteId = id
            _effects.send(
                EventsEffect.ShowUndoSnackbar(
                    message = "\"${mode.event.title}\" deleted",
                    eventId = id,
                )
            )
        }
    }

    private fun undoDelete() {
        val id = pendingDeleteId ?: return
        pendingDeleteId = null
        viewModelScope.launch { restore(id) }
    }

    /** Called by the UI when the Undo snackbar times out (10 seconds). */
    fun onUndoExpired(eventId: String) {
        if (pendingDeleteId == eventId) {
            pendingDeleteId = null
            viewModelScope.launch { hardDelete(eventId) }
        }
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
