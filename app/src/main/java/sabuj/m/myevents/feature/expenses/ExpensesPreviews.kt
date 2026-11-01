package sabuj.m.myevents.feature.expenses

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.domain.model.Expense

@LightDarkPreviews
@Composable
private fun AddExpenseSheetPreview() {
    AppTheme {
        AddEditExpenseSheet(
            sheetState             = ExpenseSheetState.Add(
                purpose          = "Flower decoration",
                selectedEventIds = setOf(PreviewData.sampleEvents.first().id),
                totalAmountPaise = 30_000_00L,
                amountDuePaise   = 10_000_00L,
                items            = listOf("Marigold", "Roses", "Backdrop"),
            ),
            events                 = PreviewData.sampleEvents,
            onDismiss              = {},
            onPurposeChange        = {},
            onToggleEventSelection = {},
            onTotalAmountChange    = {},
            onAmountDueChange      = {},
            onItemChange           = { _, _ -> },
            onRemoveItem           = {},
            onAllReceivedChange    = {},
            onSave                 = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun EditExpenseSheetPreview() {
    AppTheme {
        AddEditExpenseSheet(
            sheetState             = ExpenseSheetState.Edit(
                expenseId        = "exp-preview-1",
                eventId          = PreviewData.sampleEvents.first().id,
                purpose          = "Stage Flower Decor",
                totalAmountPaise = 30_000_00L,
                amountDuePaise   = 10_000_00L,
                items            = listOf("Marigold", "Roses", "Backdrop"),
                allReceived      = false,
            ),
            events                 = PreviewData.sampleEvents,
            onDismiss              = {},
            onPurposeChange        = {},
            onToggleEventSelection = {},
            onTotalAmountChange    = {},
            onAmountDueChange      = {},
            onItemChange           = { _, _ -> },
            onRemoveItem           = {},
            onAllReceivedChange    = {},
            onSave                 = {},
        )
    }
}
