package sabuj.m.myevents.feature.expenses

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme

@LightDarkPreviews
@Composable
private fun AddExpenseSheetPreview() {
    AppTheme {
        AddEditExpenseSheet(
            sheetState          = ExpenseSheetState.Add(
                purpose          = "Flower decoration",
                eventId          = PreviewData.sampleEvents.first().id,
                totalAmountPaise = 30_000_00L,
            ),
            events              = PreviewData.sampleEvents,
            onDismiss           = {},
            onPurposeChange     = {},
            onEventChange       = {},
            onTotalAmountChange = {},
            onSave              = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun EditExpenseSheetPreview() {
    AppTheme {
        AddEditExpenseSheet(
            sheetState          = ExpenseSheetState.Edit(
                expenseId        = "exp-preview-1",
                eventId          = PreviewData.sampleEvents.first().id,
                purpose          = "Stage Flower Decor",
                totalAmountPaise = 30_000_00L,
            ),
            events              = PreviewData.sampleEvents,
            onDismiss           = {},
            onPurposeChange     = {},
            onEventChange       = {},
            onTotalAmountChange = {},
            onSave              = {},
        )
    }
}
