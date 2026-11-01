package sabuj.m.myevents.feature.bookings

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme

/**
 * Light and dark @Preview composables for the Bookings feature.
 * Uses fake data from [PreviewData] — never the real database.
 */

@LightDarkPreviews
@Composable
private fun BookingCardFilledPreview() {
    AppTheme {
        BookingCard(
            booking       = PreviewData.sampleBooking,
            slot          = 0,
            editMode      = false,
            onTap         = {},
            onDeleteClick = {},
            event         = PreviewData.sampleEvents.first(),
        )
    }
}

@LightDarkPreviews
@Composable
private fun BookingCardEditModePreview() {
    AppTheme {
        BookingCard(
            booking       = PreviewData.sampleBooking,
            slot          = 2,
            editMode      = true,
            onTap         = {},
            onDeleteClick = {},
            event         = PreviewData.sampleEvents.first(),
        )
    }
}

@LightDarkPreviews
@Composable
private fun AddBookingSheetPreview() {
    AppTheme {
        AddEditBookingSheet(
            sheetState      = BookingSheetState.Add(
                bookingType      = "Photography",
                bookingIcon      = sabuj.m.myevents.core.icons.BookingIcon.Camera,
                eventId          = PreviewData.sampleEvents.first().id,
            ),
            events          = PreviewData.sampleEvents,
            onDismiss       = {},
            onTypeChange    = {},
            onIconChange    = {},
            onEventChange   = {},
            onContactChange = {},
            onPhoneChange   = {},
            onTotalChange   = {},
            onDueDateChange = {},
            onSave          = {},
            onAddPayment    = {},
            onDeletePayment = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun EditBookingSheetPreview() {
    AppTheme {
        AddEditBookingSheet(
            sheetState      = BookingSheetState.Edit.from(PreviewData.sampleBooking),
            events          = PreviewData.sampleEvents,
            onDismiss       = {},
            onTypeChange    = {},
            onIconChange    = {},
            onEventChange   = {},
            onContactChange = {},
            onPhoneChange   = {},
            onTotalChange   = {},
            onDueDateChange = {},
            onSave          = {},
            onAddPayment    = {},
            onDeletePayment = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun DeleteBookingDialogPreview() {
    AppTheme {
        DeleteBookingDialog(
            bookingTitle = "Photography",
            onDismiss    = {},
            onConfirm    = {},
        )
    }
}

