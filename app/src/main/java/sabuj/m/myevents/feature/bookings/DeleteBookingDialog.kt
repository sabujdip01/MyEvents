package sabuj.m.myevents.feature.bookings

import androidx.compose.runtime.Composable
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
