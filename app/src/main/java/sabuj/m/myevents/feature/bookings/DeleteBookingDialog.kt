package sabuj.m.myevents.feature.bookings

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.ui.AppDeleteDialog

/**
 * Confirmation dialog shown before deleting a booking (Item E6).
 * Uses universal [AppDeleteDialog].
 */
@Composable
fun DeleteBookingDialog(
    bookingTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppDeleteDialog(
        title     = "Delete booking?",
        message   = "\"$bookingTitle\" and all its payments will be permanently removed.",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
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
