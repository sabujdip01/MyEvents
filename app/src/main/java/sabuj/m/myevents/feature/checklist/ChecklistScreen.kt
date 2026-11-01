package sabuj.m.myevents.feature.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Task

@Composable
fun ChecklistScreen(
    onMenuClick: () -> Unit,
    viewModel: ChecklistViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.snackbarDismissed()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ScreenScaffold(onMenuClick = onMenuClick) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColor.primary)
                }
                return@ScreenScaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Top bar
                ChecklistTopBar(
                    isDeleteMode   = uiState.isDeleteMode,
                    onToggleDelete = { viewModel.toggleDeleteMode() },
                    onMenuClick    = onMenuClick,
                )

                Spacer(Modifier.height(Spacing.sm))

                // Event Tabs
                if (uiState.events.isNotEmpty()) {
                    TaskEventTabsRow(
                        events          = uiState.events,
                        selectedEventId = uiState.selectedEventId,
                        onTabSelect     = { viewModel.selectEventTab(it) },
                    )
                    Spacer(Modifier.height(Spacing.md))
                }

                // Tasks List
                val list = uiState.filteredTasks
                if (list.isEmpty()) {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "No tasks yet", style = titleL, color = AppColor.text2)
                            Spacer(Modifier.height(Spacing.sm))
                            Text(text = "Tap + to add a task", style = labelM, color = AppColor.text3)
                        }
                    }
                } else {
                    TasksListContent(
                        tasks               = list,
                        events              = uiState.events,
                        isDeleteMode        = uiState.isDeleteMode,
                        selectedForDelete   = uiState.selectedForDelete,
                        onSelectForDelete   = { viewModel.toggleSelectForDelete(it) },
                        onEditTask          = { viewModel.showEditSheet(it) },
                        onToggleCompleted   = { viewModel.toggleTaskCompleted(it) },
                        modifier            = Modifier.weight(1f),
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                if (uiState.isDeleteMode) viewModel.deleteSelectedTasks()
                else viewModel.showAddSheet()
            },
            containerColor = if (uiState.isDeleteMode) AppColor.danger else AppColor.primary,
            contentColor   = AppColor.onPrimary,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.lg),
        ) {
            if (uiState.isDeleteMode) {
                Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete selected")
            } else {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add task")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter),
        )
    }

    // Add / Edit sheet
    when (val sheet = uiState.sheetState) {
        is TaskSheetState.Add, is TaskSheetState.Edit -> AddEditTaskSheet(
            sheetState      = sheet,
            events          = uiState.events,
            onDismiss       = { viewModel.hideSheet() },
            onTitleChange   = { title ->
                if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(title = title) }
                else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(title = title) }
            },
            onEventChange   = { eventId ->
                if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(eventId = eventId) }
                else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(eventId = eventId) }
            },
            onDueDateChange = { dueDateEpoch ->
                if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(dueDateEpochDay = dueDateEpoch) }
                else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(dueDateEpochDay = dueDateEpoch) }
            },
            onSave          = { viewModel.saveTask() },
        )
        else -> Unit
    }
}

@Composable
private fun ChecklistTopBar(
    isDeleteMode: Boolean,
    onToggleDelete: () -> Unit,
    onMenuClick: () -> Unit,
) {
    sabuj.m.myevents.core.ui.AppTopBar(
        titleText   = "Checklist",
        onMenuClick = onMenuClick,
        actions     = {
            Text(
                text     = if (isDeleteMode) "Done" else "Edit",
                style    = labelL,
                color    = AppColor.primary,
                modifier = Modifier
                    .clickable { onToggleDelete() }
                    .padding(Spacing.sm),
            )
        },
    )
}

@Composable
private fun TaskEventTabsRow(
    events: List<Event>,
    selectedEventId: String?,
    onTabSelect: (String?) -> Unit,
) {
    LazyRow(
        contentPadding      = PaddingValues(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            Surface(
                onClick = { onTabSelect(null) },
                shape   = RoundedCornerShape(Radius.chip),
                color   = if (selectedEventId == null) AppColor.primaryContainer else AppColor.surfaceVariant,
            ) {
                Text(
                    text     = "All",
                    style    = labelL,
                    color    = if (selectedEventId == null) AppColor.primary else AppColor.text2,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
        itemsIndexed(events) { _, event ->
            val isSel = selectedEventId == event.id
            Surface(
                onClick = { onTabSelect(event.id) },
                shape   = RoundedCornerShape(Radius.chip),
                color   = if (isSel) AppColor.primaryContainer else AppColor.surfaceVariant,
            ) {
                Text(
                    text     = event.title,
                    style    = labelL,
                    color    = if (isSel) AppColor.primary else AppColor.text2,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun TasksListContent(
    tasks: List<Task>,
    events: List<Event>,
    isDeleteMode: Boolean,
    selectedForDelete: Set<String>,
    onSelectForDelete: (String) -> Unit,
    onEditTask: (String) -> Unit,
    onToggleCompleted: (Task) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier            = modifier.fillMaxWidth(),
        contentPadding      = PaddingValues(start = Spacing.lg, end = Spacing.lg, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(tasks, key = { it.id }) { task ->
            val event = events.find { it.id == task.eventId }
            TaskRow(
                task                = task,
                eventTitle          = event?.title ?: "",
                isDeleteMode        = isDeleteMode,
                isSelectedForDelete = selectedForDelete.contains(task.id),
                onSelectForDelete   = { onSelectForDelete(task.id) },
                onEdit              = { onEditTask(task.id) },
                onToggleCompleted   = { onToggleCompleted(task) },
            )
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    eventTitle: String,
    isDeleteMode: Boolean,
    isSelectedForDelete: Boolean,
    onSelectForDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleCompleted: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.field))
            .background(AppColor.surface)
            .clickable { if (isDeleteMode) onSelectForDelete() else onEdit() }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isDeleteMode) {
            Checkbox(
                checked         = isSelectedForDelete,
                onCheckedChange = { onSelectForDelete() },
                colors          = CheckboxDefaults.colors(checkedColor = AppColor.danger),
            )
            Spacer(Modifier.width(Spacing.xs))
        } else {
            Checkbox(
                checked         = task.completed,
                onCheckedChange = { onToggleCompleted() },
                colors          = CheckboxDefaults.colors(checkedColor = AppColor.primary),
            )
            Spacer(Modifier.width(Spacing.xs))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text           = task.title,
                style          = titleS,
                color          = if (task.completed) AppColor.text3 else AppColor.text,
                textDecoration = if (task.completed) TextDecoration.LineThrough else null,
            )
            if (eventTitle.isNotBlank()) {
                Text(text = eventTitle, style = bodyM, color = AppColor.text2)
            }
        }

        if (task.dueDate != null) {
            Pill(text = DateUtils.formatShort(task.dueDate), variant = PillVariant.Default)
        }
    }
}
