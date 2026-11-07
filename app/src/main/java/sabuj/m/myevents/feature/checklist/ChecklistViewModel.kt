package sabuj.m.myevents.feature.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.usecase.AddTaskUseCase
import sabuj.m.myevents.domain.usecase.DeleteTasksUseCase
import sabuj.m.myevents.domain.usecase.EditTaskUseCase
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.ObserveTasksUseCase
import sabuj.m.myevents.domain.usecase.TaskSaveResult
import sabuj.m.myevents.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
    private val observeTasks: ObserveTasksUseCase,
    private val addTask: AddTaskUseCase,
    private val editTask: EditTaskUseCase,
    private val deleteTasks: DeleteTasksUseCase,
    private val taskRepo: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChecklistUiState())
    val uiState: StateFlow<ChecklistUiState> = _uiState.asStateFlow()

    private var pendingDeleteId: String? = null

    init {
        viewModelScope.launch {
            combine(
                observeEvents(),
                observeTasks(),
            ) { events, tasks ->
                _uiState.value.copy(
                    isLoading = false,
                    events    = events,
                    tasks     = tasks,
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectEventTab(eventId: String?) {
        _uiState.update { it.copy(selectedEventId = eventId) }
    }

    fun showAddSheet() {
        _uiState.update {
            it.copy(
                sheetState = TaskSheetState.Add(
                    eventId = "", // Item F1: Do not auto-select event name, let user select it
                )
            )
        }
    }

    fun showEditSheet(taskId: String) {
        val t = uiState.value.tasks.find { it.id == taskId } ?: return
        _uiState.update {
            it.copy(
                sheetState = TaskSheetState.Edit(
                    taskId           = t.id,
                    icon             = t.icon,
                    eventId          = t.eventId,
                    title            = t.title,
                    dueDateEpochDay  = t.dueDate?.toEpochDay(),
                    totalAmountPaise = t.totalAmountPaise,
                    items            = t.items,
                    completed        = t.completed,
                )
            )
        }
    }

    fun hideSheet() {
        _uiState.update { it.copy(sheetState = TaskSheetState.Hidden) }
    }

    fun updateAddSheetField(update: (TaskSheetState.Add) -> TaskSheetState.Add) {
        val current = _uiState.value.sheetState as? TaskSheetState.Add ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun updateEditSheetField(update: (TaskSheetState.Edit) -> TaskSheetState.Edit) {
        val current = _uiState.value.sheetState as? TaskSheetState.Edit ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun saveTask() {
        viewModelScope.launch {
            when (val sheet = _uiState.value.sheetState) {
                is TaskSheetState.Add -> {
                    val result = addTask(
                        title            = sheet.title,
                        icon             = sheet.icon,
                        eventId          = sheet.eventId,
                        dueDate          = sheet.dueDateEpochDay?.let { LocalDate.ofEpochDay(it) },
                        totalAmountPaise = sheet.totalAmountPaise,
                        items            = sheet.items,
                        completed        = sheet.completed,
                    )
                    when (result) {
                        TaskSaveResult.Success -> hideSheet()
                        TaskSaveResult.TitleBlank -> applySheetError(sheet, titleError = "Enter item list title")
                        TaskSaveResult.EventBlank -> applySheetError(sheet, eventError = "Select an event")
                    }
                }
                is TaskSheetState.Edit -> {
                    val existing = uiState.value.tasks.find { it.id == sheet.taskId } ?: return@launch
                    val result = editTask(
                        existing         = existing,
                        title            = sheet.title,
                        icon             = sheet.icon,
                        dueDate          = sheet.dueDateEpochDay?.let { LocalDate.ofEpochDay(it) },
                        totalAmountPaise = sheet.totalAmountPaise,
                        items            = sheet.items,
                        completed        = sheet.completed,
                    )
                    when (result) {
                        TaskSaveResult.Success -> hideSheet()
                        TaskSaveResult.TitleBlank -> applySheetError(sheet, titleError = "Enter item list title")
                        else -> Unit
                    }
                }
                else -> Unit
            }
        }
    }

    private fun applySheetError(
        sheet: TaskSheetState,
        titleError: String? = null,
        eventError: String? = null,
    ) {
        _uiState.update {
            it.copy(
                sheetState = when (sheet) {
                    is TaskSheetState.Add -> sheet.copy(titleError = titleError, eventError = eventError)
                    is TaskSheetState.Edit -> sheet.copy(titleError = titleError)
                    else -> sheet
                }
            )
        }
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            editTask(
                existing  = task,
                completed = !task.completed,
            )
        }
    }

    fun toggleDeleteMode() {
        _uiState.update {
            it.copy(
                isDeleteMode      = !it.isDeleteMode,
                selectedForDelete = emptySet(),
            )
        }
    }

    fun toggleSelectForDelete(taskId: String) {
        _uiState.update { state ->
            val set = state.selectedForDelete.toMutableSet()
            if (set.contains(taskId)) set.remove(taskId) else set.add(taskId)
            state.copy(selectedForDelete = set)
        }
    }

    fun requestDeleteTask(id: String) {
        _uiState.update { it.copy(deleteTaskId = id) }
    }

    fun cancelDeleteTask() {
        _uiState.update { it.copy(deleteTaskId = null) }
    }

    fun confirmDeleteTask() {
        val id = uiState.value.deleteTaskId ?: return
        val title = uiState.value.tasks.find { it.id == id }?.title ?: "Item list"
        _uiState.update { it.copy(deleteTaskId = null, isDeleteMode = false) }
        viewModelScope.launch {
            deleteTasks(id)
            pendingDeleteId = id
            _uiState.update { it.copy(snackbarMessage = "\"$title\" deleted") }
        }
    }

    fun undoDelete() {
        val id = pendingDeleteId ?: return
        pendingDeleteId = null
        viewModelScope.launch {
            taskRepo.restoreTask(id)
        }
    }

    fun deleteSelectedTasks() {
        val ids = uiState.value.selectedForDelete.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            deleteTasks(ids)
            _uiState.update {
                it.copy(
                    isDeleteMode      = false,
                    selectedForDelete = emptySet(),
                    snackbarMessage   = "${ids.size} item lists deleted",
                )
            }
        }
    }

    fun snackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
