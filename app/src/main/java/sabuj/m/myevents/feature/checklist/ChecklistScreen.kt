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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.icons.AppIcon
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.PaletteId
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.gradientChipColor
import sabuj.m.myevents.core.theme.gradientFor
import sabuj.m.myevents.core.theme.gradientIconColor
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.numberL
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.AppDeleteDialog
import sabuj.m.myevents.core.ui.AppFab
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.IconChip
import sabuj.m.myevents.core.ui.IconChipSize
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
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
                        tasks      = list,
                        events     = uiState.events,
                        onEditTask = { viewModel.showEditSheet(it) },
                        modifier   = Modifier.weight(1f),
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
            onItemChange    = { index, name, pricePaise, checked ->
                val currentItems = (sheet as? TaskSheetState.Add)?.items ?: (sheet as? TaskSheetState.Edit)?.items ?: emptyList()
                val updatedList = currentItems.toMutableList()
                if (index in updatedList.indices) {
                    updatedList[index] = sabuj.m.myevents.domain.model.TaskItem(name, pricePaise, checked)
                    val sumPaise = updatedList.sumOf { it.pricePaise }
                    if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise) }
                    else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise) }
                }
            },
            onAddItem       = {
                val currentItems = (sheet as? TaskSheetState.Add)?.items ?: (sheet as? TaskSheetState.Edit)?.items ?: emptyList()
                val updatedList = currentItems + sabuj.m.myevents.domain.model.TaskItem("")
                if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(items = updatedList) }
                else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(items = updatedList) }
            },
            onRemoveItem    = { index ->
                val currentItems = (sheet as? TaskSheetState.Add)?.items ?: (sheet as? TaskSheetState.Edit)?.items ?: emptyList()
                if (currentItems.size > 1) {
                    val updatedList = currentItems.toMutableList().also { it.removeAt(index) }
                    val sumPaise = updatedList.sumOf { it.pricePaise }
                    if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise) }
                    else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise) }
                }
            },
            onCompletedChange = { completed ->
                if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(completed = completed) }
                else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(completed = completed) }
            },
            onSave          = { viewModel.saveTask() },
        )
        else -> Unit
    }

    // Item G5 — Universal Delete Dialog
    val deleteTaskId = uiState.deleteTaskId
    if (deleteTaskId != null) {
        val task = uiState.tasks.find { it.id == deleteTaskId }
        AppDeleteDialog(
            title     = "Delete Item List?",
            message   = "\"${task?.title ?: "This item list"}\" will be permanently deleted.",
            onDismiss = { viewModel.cancelDeleteTask() },
            onConfirm = { viewModel.confirmDeleteTask() },
        )
    }
}

@Composable
private fun TaskEventTabsRow(
    events: List<Event>,
    selectedEventId: String?,
    onTabSelect: (String?) -> Unit,
) {
    LazyRow(
        contentPadding        = PaddingValues(horizontal = Spacing.sm),
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
    onEditTask: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier       = modifier,
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        itemsIndexed(tasks, key = { _, task -> task.id }) { index, task ->
            val event = events.find { it.id == task.eventId }
            ItemListCard(
                task        = task,
                event       = event,
                slot        = index % 6,
                onEditClick = { onEditTask(task.id) },
            )
        }
    }
}

/**
 * Item List Card matching Item F3 layout specifications:
 * - Row 1: [Title] (left) | [total Amount] (middle) | <edit> pencil icon (right)
 * - Row 2,3,4: Logo left | Total Items X . Rem X & Progress Bar middle
 * - Row 5: <Event Name> (left) | <Complete by Chip> (right)
 */
@Composable
private fun ItemListCard(
    task: Task,
    event: Event?,
    slot: Int,
    onEditClick: () -> Unit,
) {
    val isDark = AppColor.isDark
    val palette = PaletteId.A
    val gradient: Brush = remember(slot, isDark) { gradientFor(slot, palette, isDark) }
    val chipColor = remember(slot, isDark) { gradientChipColor(slot, palette, isDark) }
    val iconColor = remember(slot, isDark) { gradientIconColor(slot, palette, isDark) }

    val totalItems = task.totalItems
    val remItems = task.remainingItems
    val progress = if (totalItems > 0) ((totalItems - remItems).toFloat() / totalItems.toFloat()).coerceIn(0f, 1f) else (if (task.completed) 1f else 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.card))
            .background(gradient)
            .padding(Spacing.md),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row 1: [Title] (left) | [total Amount] (middle) | <edit> pencil icon (right)
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
                Text(
                    text  = "${MoneyFormatter.format(task.totalAmountPaise)} total",
                    style = numberL,
                    color = AppColor.primary,
                )
                Spacer(Modifier.width(Spacing.md))
                Icon(
                    imageVector        = Icons.Rounded.Edit,
                    contentDescription = "Edit Item List",
                    tint               = AppColor.primary,
                    modifier           = Modifier.size(20.dp).clickable { onEditClick() },
                )
            }

            Spacer(Modifier.height(Spacing.xs))

            // Body: Logo Left + Progress Bar Middle
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconChip(
                    icon               = event?.icon?.fallback ?: AppIcon.Checklist.fallback,
                    contentDescription = event?.title ?: "",
                    size               = IconChipSize.Default,
                    containerColor     = chipColor,
                    iconColor          = iconColor,
                )

                Spacer(Modifier.width(Spacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = "Total Items $totalItems • Rem $remItems",
                        style = bodyM,
                        color = AppColor.text2,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    LinearProgressIndicator(
                        progress   = { progress },
                        modifier   = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color      = if (progress >= 1f) AppColor.success else AppColor.accent,
                        trackColor = AppColor.surface.copy(alpha = 0.4f),
                    )
                }
            }

            Spacer(Modifier.height(Spacing.sm))

            // Footer: Event Name Left + Complete By Chip Right
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
                        daysLeft < 0L  -> PillVariant.Danger
                        daysLeft <= 3L -> PillVariant.Warning
                        else           -> PillVariant.Default
                    }
                    Pill(
                        text    = "Complete by ${DateUtils.formatShort(task.dueDate)}",
                        variant = variant,
                    )
                }
            }
        }
    }
}
