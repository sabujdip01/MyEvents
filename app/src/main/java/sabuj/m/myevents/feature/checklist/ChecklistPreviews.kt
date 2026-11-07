package sabuj.m.myevents.feature.checklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import sabuj.m.myevents.core.preview.LightDarkPreviews
import sabuj.m.myevents.core.preview.PreviewData
import sabuj.m.myevents.core.theme.AppTheme
import sabuj.m.myevents.core.theme.Spacing

@LightDarkPreviews
@Composable
private fun ItemListCardNormalPreview() {
    val sampleTask = sabuj.m.myevents.domain.model.Task(
        id               = "task-preview-1",
        eventId          = "evt-preview-1",
        title            = "Finalize Mandap Decorator",
        dueDate          = java.time.LocalDate.now().plusDays(10),
        totalAmountPaise = 45_000_00L,
        items            = listOf(
            sabuj.m.myevents.domain.model.TaskItem("Flowers", 15_000_00L, true),
            sabuj.m.myevents.domain.model.TaskItem("Lighting", 15_000_00L, true),
            sabuj.m.myevents.domain.model.TaskItem("Backdrop", 15_000_00L, false),
        ),
        completed        = false,
        createdAt        = System.currentTimeMillis(),
        updatedAt        = System.currentTimeMillis(),
        syncState        = sabuj.m.myevents.domain.model.SyncState.PENDING,
        deleted          = false,
    )
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            ItemListCard(
                task          = sampleTask,
                event         = PreviewData.sampleEvents.first(),
                slot          = 0,
                isDeleteMode  = false,
                onEditClick   = {},
                onDeleteClick = {},
            )
        }
    }
}

@LightDarkPreviews
@Composable
private fun ItemListCardDeleteModePreview() {
    val sampleTask = sabuj.m.myevents.domain.model.Task(
        id               = "task-preview-1",
        eventId          = "evt-preview-1",
        title            = "Finalize Mandap Decorator",
        dueDate          = java.time.LocalDate.now().plusDays(10),
        totalAmountPaise = 45_000_00L,
        items            = listOf(
            sabuj.m.myevents.domain.model.TaskItem("Flowers", 15_000_00L, true),
            sabuj.m.myevents.domain.model.TaskItem("Lighting", 15_000_00L, true),
            sabuj.m.myevents.domain.model.TaskItem("Backdrop", 15_000_00L, false),
        ),
        completed        = false,
        createdAt        = System.currentTimeMillis(),
        updatedAt        = System.currentTimeMillis(),
        syncState        = sabuj.m.myevents.domain.model.SyncState.PENDING,
        deleted          = false,
    )
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
        ) {
            ItemListCard(
                task          = sampleTask,
                event         = PreviewData.sampleEvents.first(),
                slot          = 1,
                isDeleteMode  = true,
                onEditClick   = {},
                onDeleteClick = {},
            )
        }
    }
}

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
                icon            = sabuj.m.myevents.core.icons.BookingIcon.Cake,
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
