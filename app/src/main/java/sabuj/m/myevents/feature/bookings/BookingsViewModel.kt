package sabuj.m.myevents.feature.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.usecase.AddBookingUseCase
import sabuj.m.myevents.domain.usecase.AddPaymentUseCase
import sabuj.m.myevents.domain.usecase.BookingSaveResult
import sabuj.m.myevents.domain.usecase.DeleteBookingUseCase
import sabuj.m.myevents.domain.usecase.EditBookingUseCase
import sabuj.m.myevents.domain.usecase.ObserveBookingsUseCase
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.PaymentSaveResult
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for [BookingsScreen].
 * Combines bookings and events flows into a single [BookingsUiState].
 * Handles all user intents via sealed event classes.
 */
@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val observeBookings: ObserveBookingsUseCase,
    private val observeEvents: ObserveEventsUseCase,
    private val addBooking: AddBookingUseCase,
    private val editBooking: EditBookingUseCase,
    private val deleteBooking: DeleteBookingUseCase,
    private val addPayment: AddPaymentUseCase,
    private val bookingRepo: BookingRepository,
) : ViewModel() {

    private val _filterEventId = MutableStateFlow<String?>(null)
    private val _mode = MutableStateFlow(BookingsMode.Normal)
    private val _sheetState = MutableStateFlow<BookingSheetState>(BookingSheetState.Hidden)
    private val _paymentDialogState = MutableStateFlow<PaymentDialogState>(PaymentDialogState.Hidden)
    private val _deleteDialogId = MutableStateFlow<String?>(null)
    private val _snackbar = MutableStateFlow<String?>(null)

    /** Id of the booking currently pending undo-delete. */
    private var pendingDeleteId: String? = null

    val uiState = combine(
        observeBookings(),
        observeEvents(),
        _filterEventId,
        _mode,
        _sheetState,
    ) { bookings, events, filterId, mode, sheet ->
        BookingsUiState(
            bookings      = bookings,
            events        = events,
            filterEventId = filterId,
            mode          = mode,
            sheetState    = sheet,
        )
    }.combine(_paymentDialogState) { state, dialog ->
        state.copy(paymentDialogState = dialog)
    }.combine(_deleteDialogId) { state, deleteId ->
        state.copy(deleteDialogBookingId = deleteId)
    }.combine(_snackbar) { state, msg ->
        state.copy(snackbarMessage = msg)
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = BookingsUiState(isLoading = true),
    )

    // â”€â”€ Intent handlers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /** Set the active event filter (null = show all). */
    fun filterByEvent(eventId: String?) {
        _filterEventId.value = eventId
    }

    /** Open the Add Booking sheet, pre-selecting the first event if only one exists. */
    fun openAddSheet() {
        val events = uiState.value.events
        val preselected = if (events.size == 1) events.first().id else ""
        _sheetState.value = BookingSheetState.Add(eventId = preselected)
    }

    /** Open the Edit Booking sheet for an existing booking. */
    fun openEditSheet(booking: Booking) {
        _sheetState.value = BookingSheetState.Edit.from(booking)
    }

    /** Close any open sheet. */
    fun hideSheet() {
        _sheetState.value = BookingSheetState.Hidden
    }

    /** Update a field in the currently open Add sheet. */
    fun updateAddSheetField(update: (BookingSheetState.Add) -> BookingSheetState.Add) {
        val current = _sheetState.value as? BookingSheetState.Add ?: return
        _sheetState.value = update(current)
    }

    /** Update a field in the currently open Edit sheet. */
    fun updateEditSheetField(update: (BookingSheetState.Edit) -> BookingSheetState.Edit) {
        val current = _sheetState.value as? BookingSheetState.Edit ?: return
        _sheetState.value = update(current)
    }

    /** Validate and save the booking from the current sheet state. */
    fun saveBooking() {
        viewModelScope.launch {
            when (val sheet = _sheetState.value) {
                is BookingSheetState.Add -> {
                    val result = addBooking(
                        title              = sheet.bookingType,
                        bookingType        = sheet.bookingType,
                        bookingIcon        = sheet.bookingIcon ?: BookingIcon.Cake,
                        eventId            = sheet.eventId,
                        contactName        = sheet.contactName,
                        phone              = sheet.phone,
                        totalAmountPaise   = sheet.totalAmountPaise,
                        bookingAmountPaise = sheet.bookingAmountPaise,
                        nextDueDate        = sheet.nextDueDateEpochDay?.let { LocalDate.ofEpochDay(it) },
                        bookingMade        = sheet.bookingMade,
                    )
                    handleSaveResult(result, sheet)
                }
                is BookingSheetState.Edit -> {
                    val existing = uiState.value.bookings.find { it.id == sheet.bookingId } ?: return@launch
                    val result = editBooking(
                        existing         = existing,
                        title            = sheet.bookingType,
                        bookingType      = sheet.bookingType,
                        bookingIcon      = sheet.bookingIcon ?: BookingIcon.Cake,
                        eventId          = sheet.eventId,
                        contactName      = sheet.contactName,
                        phone            = sheet.phone,
                        totalAmountPaise = sheet.totalAmountPaise,
                        nextDueDate      = sheet.nextDueDateEpochDay?.let { LocalDate.ofEpochDay(it) },
                        bookingMade      = sheet.bookingMade,
                        payments         = sheet.payments,
                    )
                    handleSaveResult(result, sheet)
                }
                else -> Unit
            }
        }
    }

    private fun handleSaveResult(result: BookingSaveResult, sheet: BookingSheetState) {
        when (result) {
            is BookingSaveResult.Success -> _sheetState.value = BookingSheetState.Hidden
            is BookingSaveResult.EventIdBlank -> applySheetError(sheet, eventError = "Select an Event")
            is BookingSaveResult.TotalInvalid -> applySheetError(sheet, totalError = "Enter a Valid Total")
        }
    }

    private fun applySheetError(
        sheet: BookingSheetState,
        eventError: String? = null,
        totalError: String? = null,
    ) {
        _sheetState.value = when (sheet) {
            is BookingSheetState.Add -> sheet.copy(
                eventError = eventError,
                totalError = totalError,
            )
            is BookingSheetState.Edit -> sheet.copy(
                eventError = eventError,
                totalError = totalError,
            )
            else -> sheet
        }
    }

    /** Toggle between Normal and Edit mode. */
    fun toggleEditMode() {
        _mode.update { if (it == BookingsMode.Normal) BookingsMode.Edit else BookingsMode.Normal }
    }

    /** Show the delete confirmation dialog for a booking. */
    fun requestDelete(bookingId: String) {
        _deleteDialogId.value = bookingId
    }

    /** Dismiss the delete dialog without deleting. */
    fun dismissDeleteDialog() {
        _deleteDialogId.value = null
    }

    /** Confirm the delete: soft-delete then schedule hard-delete after 10 s. */
    fun confirmDelete() {
        val id = _deleteDialogId.value ?: return
        _deleteDialogId.value = null
        _mode.value = BookingsMode.Normal
        viewModelScope.launch {
            deleteBooking.softDelete(id)
            pendingDeleteId = id
            val title = uiState.value.bookings.find { it.id == id }?.title ?: "Booking"
            _snackbar.value = "\"$title\" deleted"
            // Schedule hard-delete after 10 seconds unless undone
            launch {
                delay(10_000L)
                if (pendingDeleteId == id) {
                    pendingDeleteId = null
                    deleteBooking.hardDelete(id)
                }
            }
        }
    }

    /** Undo the pending soft-delete within the undo window. */
    fun undoDelete() {
        val id = pendingDeleteId ?: return
        pendingDeleteId = null
        viewModelScope.launch {
            bookingRepo.restoreDeleted(id)
        }
    }

    /** Dismiss the snackbar. */
    fun snackbarDismissed() {
        _snackbar.value = null
    }

    /** Open the Add Payment dialog for a booking. */
    fun openPaymentDialog(bookingId: String) {
        _paymentDialogState.value = PaymentDialogState.Add(bookingId = bookingId)
    }

    /** Close the payment dialog. */
    fun hidePaymentDialog() {
        _paymentDialogState.value = PaymentDialogState.Hidden
    }

    /** Update a field in the payment dialog. */
    fun updatePaymentField(update: (PaymentDialogState.Add) -> PaymentDialogState.Add) {
        val current = _paymentDialogState.value as? PaymentDialogState.Add ?: return
        _paymentDialogState.value = update(current)
    }

    /** Validate and append a payment to the local edit sheet state (commits to DB on Save). */
    fun savePayment() {
        val dialog = _paymentDialogState.value as? PaymentDialogState.Add ?: return
        if (dialog.amountPaise <= 0L) {
            _paymentDialogState.value = dialog.copy(amountError = "Enter a Valid Amount")
            return
        }
        val newPayment = sabuj.m.myevents.domain.model.BookingPayment(
            id          = java.util.UUID.randomUUID().toString(),
            bookingId   = dialog.bookingId,
            date        = LocalDate.ofEpochDay(dialog.dateEpochDay),
            amountPaise = dialog.amountPaise,
            note        = dialog.note,
            createdAt   = System.currentTimeMillis(),
            updatedAt   = System.currentTimeMillis(),
        )
        _paymentDialogState.value = PaymentDialogState.Hidden
        updateEditSheetField { edit ->
            edit.copy(payments = edit.payments + newPayment)
        }
    }

    /** Remove a payment row locally from the edit sheet state (commits to DB on Save). */
    fun deletePaymentLocal(paymentId: String) {
        updateEditSheetField { edit ->
            edit.copy(payments = edit.payments.filterNot { it.id == paymentId })
        }
    }

    /** Delete a single payment by id. */
    fun deletePayment(paymentId: String) {
        viewModelScope.launch {
            bookingRepo.deletePayment(paymentId)
        }
    }
}
