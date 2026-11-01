package sabuj.m.myevents.feature.checklist

import androidx.compose.runtime.Composable
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme

@LightDarkPreviews
@Composable
private fun AddTaskSheetPreview() {
    AppTheme {
        AddEditTaskSheet(
            sheetState      = TaskSheetState.Add(
                title   = "Finalize Mandap Decorator",
                eventId = PreviewData.sampleEvents.first().id,
            ),
            events          = PreviewData.sampleEvents,
            onDismiss       = {},
            onTitleChange   = {},
            onEventChange   = {},
            onDueDateChange = {},
            onSave          = {},
        )
    }
}

@LightDarkPreviews
@Composable
private fun EditTaskSheetPreview() {
    AppTheme {
        AddEditTaskSheet(
            sheetState      = TaskSheetState.Edit(
                taskId          = "task-1",
                eventId         = PreviewData.sampleEvents.first().id,
                title           = "Finalize Mandap Decorator",
                dueDateEpochDay = null,
                completed       = false,
            ),
            events          = PreviewData.sampleEvents,
            onDismiss       = {},
            onTitleChange   = {},
            onEventChange   = {},
            onDueDateChange = {},
            onSave          = {},
        )
    }
}
