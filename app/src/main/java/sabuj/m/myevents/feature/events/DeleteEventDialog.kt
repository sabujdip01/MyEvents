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
        title     = "Delete event?",
        message   = "\"${event.title}\" and all its linked bookings, expenses, invitees and tasks will be removed. You have 10 seconds to undo.",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}
