package sabuj.m.myevents.feature.checklist

import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Task

sealed class TaskSheetState {
    object Hidden : TaskSheetState()

    data class Add(
        val title: String = "",
        val eventId: String = "",
        val dueDateEpochDay: Long? = null,
        val totalAmountPaise: Long = 0L,
        val items: List<sabuj.m.myevents.domain.model.TaskItem> = listOf(sabuj.m.myevents.domain.model.TaskItem("")),
        val completed: Boolean = false,
        val titleError: String? = null,
        val eventError: String? = null,
    ) : TaskSheetState()

    data class Edit(
        val taskId: String,
        val eventId: String,
        val title: String,
        val dueDateEpochDay: Long?,
        val totalAmountPaise: Long = 0L,
        val items: List<sabuj.m.myevents.domain.model.TaskItem> = emptyList(),
        val completed: Boolean = false,
        val titleError: String? = null,
        val eventError: String? = null,
    ) : TaskSheetState()
}

/** UI state for Checklist screen. */
data class ChecklistUiState(
    val isLoading: Boolean = true,
    val events: List<Event> = emptyList(),
    val selectedEventId: String? = null, // null = All
    val tasks: List<Task> = emptyList(),
    val sheetState: TaskSheetState = TaskSheetState.Hidden,
    val isDeleteMode: Boolean = false,
    val selectedForDelete: Set<String> = emptySet(),
    val deleteTaskId: String? = null,
    val snackbarMessage: String? = null,
) {
    /** Filtered list of tasks for the selected event. */
    val filteredTasks: List<Task>
        get() = if (selectedEventId == null) tasks else tasks.filter { it.eventId == selectedEventId }
}
