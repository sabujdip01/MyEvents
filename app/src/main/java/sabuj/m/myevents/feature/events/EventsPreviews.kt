package sabuj.m.myevents.feature.events

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme
import java.time.YearMonth

/**
 * Previews for the Events screen calendar month grid.
 * Uses [PreviewData.sampleEventsByDate] to show events on the grid.
 */
@LightDarkPreviews
@Composable
private fun MonthGridPreview() {
    AppTheme {
        MonthGrid(
            month           = YearMonth.now(),
            today           = java.time.LocalDate.now(),
            eventsByDate    = PreviewData.sampleEventsByDate,
            onDateTap       = {},
            onDateLongPress = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun EventDetailsSheetPreview() {
    AppTheme {
        EventDetailsSheet(
            event     = PreviewData.sampleEvents.first(),
            onDismiss = {},
            onEdit    = {},
            onDelete  = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun AddEventSheetPreview() {
    AppTheme {
        AddEditEventSheet(
            prefilledDate = java.time.LocalDate.now(),
            existingEvent = null,
            onDismiss     = {},
            onSave        = { _, _, _, _, _, _ -> },
        )
    }
}

