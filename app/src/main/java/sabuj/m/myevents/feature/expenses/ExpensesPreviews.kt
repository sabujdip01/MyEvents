package sabuj.m.myevents.feature.expenses

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.domain.model.Expense

@LightDarkPreviews
@Composable
private fun ExpensesScreenPreview() {
    val sampleEvents = PreviewData.sampleEvents
    val sampleExpenses = listOf(
        Expense(
            id             = "exp-1",
            eventId        = sampleEvents.first().id,
            groupId        = "",
            purpose        = "Stage Flower Decor",
            amountPaise    = 45_000_00L,
            amountDuePaise = 15_000_00L,
            items          = emptyList(),
            allReceived    = false,
            createdAt      = System.currentTimeMillis(),
            updatedAt      = System.currentTimeMillis(),
        ),
        Expense(
            id             = "exp-2",
            eventId        = sampleEvents.first().id,
            groupId        = "",
            purpose        = "Sound System & DJ",
            amountPaise    = 25_000_00L,
            amountDuePaise = 0L,
            items          = emptyList(),
            allReceived    = true,
            createdAt      = System.currentTimeMillis(),
            updatedAt      = System.currentTimeMillis(),
        ),
    )
    val state = ExpensesUiState(
        isLoading = false,
        events    = sampleEvents,
        expenses  = sampleExpenses,
    )
    AppTheme {
        ExpensesContent(
            uiState     = state,
            onMenuClick = {},
        )
    }
}
