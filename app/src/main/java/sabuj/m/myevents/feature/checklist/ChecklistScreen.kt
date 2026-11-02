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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.numberL
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.AppFab
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Task
import java.time.LocalDate

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

            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar (Item G2 — Edit option removed)
                AppTopBar(
                    titleText   = "Item Lists",
                    onMenuClick = onMenuClick,
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

                // Item Lists
                val list = uiState.filteredTasks
                if (list.isEmpty()) {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "No item lists yet", style = titleL, color = AppColor.text2)
                            Spacer(Modifier.height(Spacing.sm))
                            Text(text = "Tap + to add an item list", style = labelM, color = AppColor.text3)
                        }
                    }
                } else {
                    ItemListContent(
                        tasks             = list,
                        events            = uiState.events,
                        onEditTask        = { viewModel.showEditSheet(it.id) },
                        onToggleCompleted = { viewModel.toggleTaskCompleted(it) },
                        modifier          = Modifier.weight(1f),
                    )
                }
            }
        }

        // FAB (Items G1 & F2 — Round AppFab with navigationBarsPadding)
        AppFab(
            onClick            = { viewModel.showAddSheet() },
            icon               = Icons.Rounded.Add,
            contentDescription = "Add Item List",
            modifier           = Modifier.align(Alignment.BottomEnd),
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp),
        )
    }

    // Sheet / Dialogs
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
private fun TaskEventTabsRow(
    events: List<Event>,
    selectedEventId: String?,
    onTabSelect: (String?) -> Unit,
) {
    LazyRow(
        contentPadding      = PaddingValues(horizontal = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            val isSelected = selectedEventId == null
            Surface(
                onClick  = { onTabSelect(null) },
                shape    = RoundedCornerShape(Radius.chip),
                color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
                modifier = Modifier.height(36.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = Spacing.md)) {
                    Text("All", style = labelM, color = if (isSelected) AppColor.primary else AppColor.text2)
                }
            }
        }

        itemsIndexed(events) { _, event ->
            val isSelected = selectedEventId == event.id
            Surface(
                onClick  = { onTabSelect(event.id) },
                shape    = RoundedCornerShape(Radius.chip),
                color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
                modifier = Modifier.height(36.dp),
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(event.icon.fallback, null, tint = if (isSelected) AppColor.primary else AppColor.text2, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(Spacing.xs))
                    Text(event.title, style = labelM, color = if (isSelected) AppColor.primary else AppColor.text2)
                }
            }
        }
    }
}

@Composable
private fun ItemListContent(
    tasks: List<Task>,
    events: List<Event>,
    onEditTask: (Task) -> Unit,
    onToggleCompleted: (Task) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { LocalDate.now() }

    LazyColumn(
        modifier       = modifier,
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        itemsIndexed(tasks, key = { _, task -> task.id }) { _, task ->
            val event = events.find { it.id == task.eventId }
            ItemListCard(
                task              = task,
                event             = event,
                today             = today,
                onEditClick       = { onEditTask(task) },
                onToggleCompleted = { onToggleCompleted(task) },
            )
        }
    }
}

/**
 * Item List Card matching F4 / G5 layout specifications:
 * - Title + Amount/Status + Pencil Edit Icon top
 * - Logo left + Total Items/Rem count + Progress Bar middle
 * - Event Name bottom-left + Complete By Chip bottom-right
 */
@Composable
private fun ItemListCard(
    task: Task,
    event: Event?,
    today: LocalDate,
    onEditClick: () -> Unit,
    onToggleCompleted: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(Radius.card),
        color    = AppColor.surface,
        shadowElevation = 2.dp,
        border   = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else AppColor.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            // Header Row: Title + Paid/Status + Pencil Edit Icon
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = task.title,
                    style    = titleS,
                    color    = AppColor.text,
                    modifier = Modifier.weight(1f),
                )
                Pill(
                    text    = if (task.completed) "Completed" else "In Progress",
                    variant = if (task.completed) PillVariant.Success else PillVariant.Warning,
                )
                Spacer(Modifier.width(Spacing.xs))
                Icon(
                    imageVector        = Icons.Rounded.Edit,
                    contentDescription = "Edit Item List",
                    tint               = AppColor.primary,
                    modifier           = Modifier.size(20.dp).clickable { onEditClick() },
                )
            }

            Spacer(Modifier.height(Spacing.sm))

            // Body Row: Logo Left + Progress Bar Middle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier        = Modifier.size(44.dp).clip(CircleShape).background(AppColor.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = event?.icon?.fallback ?: AppIcon.Checklist.fallback,
                        contentDescription = null,
                        tint               = AppColor.primary,
                        modifier           = Modifier.size(22.dp),
                    )
                }

                Spacer(Modifier.width(Spacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = if (task.completed) "All items complete" else "Items pending",
                        style = bodyM,
                        color = AppColor.text2,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    LinearProgressIndicator(
                        progress   = { if (task.completed) 1f else 0.5f },
                        modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color      = if (task.completed) AppColor.success else AppColor.primary,
                        trackColor = AppColor.primaryContainer,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            // Footer Row: Event Name Left + Complete By Chip Right
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text     = event?.title ?: "General",
                    style    = bodyM,
                    color    = AppColor.text2,
                    modifier = Modifier.weight(1f),
                )

                if (task.dueDate != null) {
                    val daysLeft = DateUtils.daysUntil(task.dueDate)
                    val variant = when {
                        daysLeft < 0L -> PillVariant.Danger
                        daysLeft <= 3L -> PillVariant.Warning
                        else -> PillVariant.Default
                    }
                    Pill(
                        text = "Complete by ${DateUtils.formatShort(task.dueDate)}",
                        variant = variant,
                    )
                }

                Spacer(Modifier.width(Spacing.xs))
                Checkbox(
                    checked         = task.completed,
                    onCheckedChange = { onToggleCompleted() },
                    colors          = CheckboxDefaults.colors(
                        checkedColor   = AppColor.primary,
                        uncheckedColor = AppColor.outline,
                    ),
                )
            }
        }
    }
}
