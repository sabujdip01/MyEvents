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
private fun EventsScreenFilledPreview() {
    val sampleEvents = listOf(
        sabuj.m.myevents.domain.model.Event(
            id               = "evt-preview-1",
            title            = "Wedding",
            eventName        = "Wedding",
            date             = java.time.LocalDate.now().plusDays(22),
            icon             = sabuj.m.myevents.core.icons.EventIcon.Wedding,
            budgetPaise      = 500_000_00L,
            inviteesRequired = true,
            createdAt        = System.currentTimeMillis(),
            updatedAt        = System.currentTimeMillis(),
            syncState        = sabuj.m.myevents.domain.model.SyncState.PENDING,
        ),
    )
    AppTheme {
        EventsContent(
            uiState = EventsUiState(
                currentMonth = YearMonth.now(),
                events       = sampleEvents,
                eventsByDate = sampleEvents.associateBy { it.date.toEpochDay() },
                isLoading    = false,
            ),
            onMenuClick = {},
            onEvent     = {},
        )
    }
}

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
        EventDetailsContent(
            event       = PreviewData.sampleEvents.first(),
            showActions = true,
        )
    }
}

@LightDarkPreviews
@Composable
private fun EventDetailsSheetReadOnlyPreview() {
    AppTheme {
        EventDetailsContent(
            event       = PreviewData.sampleEvents.first(),
            showActions = false,
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

