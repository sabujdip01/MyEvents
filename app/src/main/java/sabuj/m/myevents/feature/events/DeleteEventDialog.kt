package sabuj.m.myevents.feature.events

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme
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
        message   = "\"${event.title}\" and all its linked bookings, expenses, invitees and tasks will be completely removed.",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
    )
}

@LightDarkPreviews
@Composable
private fun DeleteEventDialogPreview() {
    AppTheme {
        DeleteEventDialog(
            event     = PreviewData.sampleEvents.first(),
            onDismiss = {},
            onConfirm = {},
        )
    }
}
