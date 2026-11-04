package sabuj.m.myevents.feature.events

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.ui.AppDeleteDialog
import sabuj.m.myevents.domain.model.Event

/**
 * Delete confirmation dialog shown before deleting an event (Item D6).
 * Uses universal [AppDeleteDialog].
 */
@Composable
fun DeleteEventDialog(
    event: Event,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppDeleteDialog(
        title     = "Wait a sec!",
        message   = "Event \"${event.title}\" and its linked Bookings, Expenses, Guests details will be completely removed.\n\nThis action is irreversible.",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}
