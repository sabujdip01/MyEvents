package sabuj.m.myevents.feature.events

import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate
import java.time.YearMonth

/**
 * Immutable UI state for the Events screen.
 *
 * @param currentMonth   The month currently shown in the calendar pager.
 * @param events         All live events (non-deleted).
 * @param eventsByDate   Convenience map: epochDay → Event for fast lookup in the grid.
 * @param selectedDate   Date the user last tapped (drives the bottom sheet).
 * @param sheetMode      Which sheet is open, if any.
 * @param isLoading      True while the initial DB query is in flight.
 * @param errorMessage   Non-null when an operation failed.
 */
data class EventsUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val events: List<Event> = emptyList(),
    val eventsByDate: Map<Long, Event> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val sheetMode: SheetMode = SheetMode.None,
    val bookingCount: Int = 0,
    val inviteeCount: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val snackbarMessage: String? = null,
    val lastDeletedEventId: String? = null,
)

/** Controls which bottom sheet is shown. */
sealed interface SheetMode {
    /** No sheet open. */
    data object None : SheetMode
    /** Details popup for an existing event (tap on event date). */
    data class Details(val event: Event) : SheetMode
    /** Add-event form (long-press on empty date). */
    data class Add(val date: LocalDate) : SheetMode
    /** Edit-event form (long-press on event date or tap Edit in Details). */
    data class Edit(val event: Event) : SheetMode
    /** Delete-confirm dialog. */
    data class ConfirmDelete(val event: Event) : SheetMode
}

/** User-intent events for the Events screen. */
sealed interface EventsUiEvent {
    data class MonthChanged(val month: YearMonth) : EventsUiEvent
    data class DateTapped(val date: LocalDate) : EventsUiEvent
    data class DateLongPressed(val date: LocalDate) : EventsUiEvent
    data object SheetDismissed : EventsUiEvent
    data class EditRequested(val event: Event) : EventsUiEvent
    data class DeleteRequested(val event: Event) : EventsUiEvent
    data object DeleteConfirmed : EventsUiEvent
    data object UndoDelete : EventsUiEvent
    data object SnackbarDismissed : EventsUiEvent

    data class SaveNew(
        val title: String,
        val eventName: String,
        val date: LocalDate,
        val icon: EventIcon,
        val budgetPaise: Long,
        val inviteesRequired: Boolean,
    ) : EventsUiEvent

    data class SaveEdit(
        val title: String,
        val eventName: String,
        val date: LocalDate,
        val icon: EventIcon,
        val budgetPaise: Long,
        val inviteesRequired: Boolean,
    ) : EventsUiEvent
}
