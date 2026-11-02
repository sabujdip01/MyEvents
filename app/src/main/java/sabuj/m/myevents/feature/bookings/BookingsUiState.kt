package sabuj.m.myevents.feature.bookings

import sabuj.m.myevents.core.icons.BookingIcon
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import java.time.LocalDate

/**
 * Immutable UI state for the Bookings screen.
 *
 * @param bookings            All live (non-deleted) bookings.
 * @param events              All events (for the event dropdown in Add/Edit).
 * @param isLoading           True while initial DB queries are in flight.
 * @param filterEventId       Null = show all; non-null = show only that event's bookings.
 * @param mode                Normal browsing vs. edit-mode (delete badges visible).
 * @param sheetState          Which sheet is open, if any.
 * @param paymentDialogState  Which payment dialog is open, if any.
 * @param deleteDialogBookingId Non-null when the delete confirmation dialog is showing.
 * @param snackbarMessage     Non-null when a snackbar should be shown.
 */
data class BookingsUiState(
    val bookings: List<Booking> = emptyList(),
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val filterEventId: String? = null,
    val mode: BookingsMode = BookingsMode.Normal,
    val sheetState: BookingSheetState = BookingSheetState.Hidden,
    val paymentDialogState: PaymentDialogState = PaymentDialogState.Hidden,
    val deleteDialogBookingId: String? = null,
    val snackbarMessage: String? = null,
) {
    /** Bookings filtered by [filterEventId]. */
    val filteredBookings: List<Booking>
        get() = if (filterEventId == null) bookings
                else bookings.filter { it.eventId == filterEventId }
}

/** Controls whether the screen is in normal or edit (delete-badge) mode. */
enum class BookingsMode { Normal, Edit }

/** Controls which add/edit bottom sheet is visible. */
sealed class BookingSheetState {

    /** No sheet is showing. */
    object Hidden : BookingSheetState()

    /** Sheet for adding a new booking. */
    data class Add(
        val bookingType: String = "",
        val bookingIcon: BookingIcon = BookingIcon.Other,
        val eventId: String = "",
        val contactName: String = "",
        val phone: String = "",
        val totalAmountPaise: Long = 0L,
        val bookingAmountPaise: Long = 0L,
        val nextDueDateEpochDay: Long? = null,
        val bookingMade: Boolean = true,
        val eventError: String? = null,
        val totalError: String? = null,
    ) : BookingSheetState()

    /** Sheet for editing an existing booking. */
    data class Edit(
        val bookingId: String,
        val bookingType: String,
        val bookingIcon: BookingIcon,
        val eventId: String,
        val contactName: String,
        val phone: String,
        val totalAmountPaise: Long,
        val bookingAmountPaise: Long = 0L,
        val nextDueDateEpochDay: Long?,
        val bookingMade: Boolean = true,
        val payments: List<sabuj.m.myevents.domain.model.BookingPayment>,
        val eventError: String? = null,
        val totalError: String? = null,
    ) : BookingSheetState() {
        companion object {
            /** Construct an Edit state pre-filled from an existing [Booking]. */
            fun from(booking: Booking) = Edit(
                bookingId          = booking.id,
                bookingType        = booking.bookingType,
                bookingIcon        = booking.bookingIcon,
                eventId            = booking.eventId,
                contactName        = booking.contactName,
                phone              = booking.phone,
                totalAmountPaise   = booking.totalAmountPaise,
                bookingAmountPaise = 0L,
                nextDueDateEpochDay = booking.nextDueDate?.toEpochDay(),
                bookingMade        = booking.bookingMade,
                payments           = booking.payments,
            )
        }
    }
}

/** Controls the add-payment dialog visibility. */
sealed class PaymentDialogState {

    /** No dialog showing. */
    object Hidden : PaymentDialogState()

    /** Dialog for adding a payment to [bookingId]. */
    data class Add(
        val bookingId: String,
        val dateEpochDay: Long = LocalDate.now().toEpochDay(),
        val amountPaise: Long = 0L,
        val note: String = "",
        val amountError: String? = null,
    ) : PaymentDialogState()
}
