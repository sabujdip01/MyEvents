package sabuj.m.myevents.feature.itemlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sabuj.m.myevents.core.icons.EventIconDisplay
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
import sabuj.m.myevents.core.ui.AppSnackbar
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
fun ItemListsScreen(
    onMenuClick: () -> Unit,
    viewModel: ItemListsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Automatically clear snackbar if user navigates away
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            viewModel.snackbarDismissed()
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        try {
            kotlinx.coroutines.withTimeoutOrNull(5000L) {
                val result = snackbarHostState.showSnackbar(
                    message     = msg,
                    actionLabel = "UNDO",
                    duration    = SnackbarDuration.Indefinite,
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.undoDelete()
                }
            }
        } finally {
            viewModel.snackbarDismissed()
        }
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
                // Top bar with Delete/Done action toggle
                ItemListsTopBar(
                    isDeleteMode   = uiState.isDeleteMode,
                    onToggleDelete = { viewModel.toggleDeleteMode() },
                    onMenuClick    = onMenuClick,
                )

                Spacer(Modifier.height(Spacing.sm))

                // Event Tabs
                if (uiState.events.isNotEmpty()) {
                    TaskEventTabsRow(
                        events          = uiState.events,
                        tasks           = uiState.tasks,
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
                        tasks        = list,
                        events       = uiState.events,
                        isDeleteMode = uiState.isDeleteMode,
                        onEditTask   = { viewModel.showEditSheet(it) },
                        onDeleteTask = { viewModel.requestDeleteTask(it) },
                        modifier     = Modifier.weight(1f),
                    )
                }
            }
        }

        // FAB (Disabled in Delete mode)
        AppFab(
            onClick            = { viewModel.showAddSheet() },
            icon               = Icons.Rounded.Add,
            contentDescription = "Add Item List",
            enabled            = !uiState.isDeleteMode,
            modifier           = Modifier.align(Alignment.BottomEnd),
        )

        AppSnackbar(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.Center),
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
            onIconChange    = { icon ->
                if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(icon = icon) }
                else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(icon = icon) }
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
                    if (index == updatedList.lastIndex && name.isNotBlank()) {
                        updatedList.add(sabuj.m.myevents.domain.model.TaskItem(""))
                    }
                    val sumPaise = updatedList.sumOf { it.pricePaise }
                    val filled = updatedList.filter { it.name.isNotBlank() }
                    val autoCompleted = filled.isNotEmpty() && filled.all { it.checked }
                    if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise, completed = autoCompleted) }
                    else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise, completed = autoCompleted) }
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
                    val filled = updatedList.filter { it.name.isNotBlank() }
                    val autoCompleted = filled.isNotEmpty() && filled.all { it.checked }
                    if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise, completed = autoCompleted) }
                    else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(items = updatedList, totalAmountPaise = sumPaise, completed = autoCompleted) }
                }
            },
            onCompletedChange = { completed ->
                val currentItems = (sheet as? TaskSheetState.Add)?.items ?: (sheet as? TaskSheetState.Edit)?.items ?: emptyList()
                val updatedList = currentItems.map {
                    if (it.name.isNotBlank()) it.copy(checked = completed) else it
                }
                if (sheet is TaskSheetState.Add) viewModel.updateAddSheetField { it.copy(completed = completed, items = updatedList) }
                else if (sheet is TaskSheetState.Edit) viewModel.updateEditSheetField { it.copy(completed = completed, items = updatedList) }
            },
            onSave          = { viewModel.saveTask() },
        )
        else -> Unit
    }

    // Universal Delete Dialog
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
    tasks: List<Task>,
    selectedEventId: String?,
    onTabSelect: (String?) -> Unit,
) {
    val totalPaidAll = tasks.sumOf { it.totalAmountPaise }
    LazyRow(
        contentPadding        = PaddingValues(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            val isSelected = selectedEventId == null
            Surface(
                onClick  = { onTabSelect(null) },
                shape    = RoundedCornerShape(Radius.pill),
                color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
                modifier = Modifier.height(36.dp),
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = Spacing.md)) {
                    Text(
                        text  = "All (${MoneyFormatter.format(totalPaidAll)})",
                        style = labelM,
                        color = if (isSelected) AppColor.onPrimaryContainer else AppColor.text2,
                    )
                }
            }
        }

        itemsIndexed(events) { _, event ->
            val isSelected = selectedEventId == event.id
            val totalPaidForEvent = tasks.filter { it.eventId == event.id }.sumOf { it.totalAmountPaise }
            Surface(
                onClick  = { onTabSelect(event.id) },
                shape    = RoundedCornerShape(Radius.pill),
                color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
                modifier = Modifier.height(36.dp),
            ) {
                Row(
                    modifier          = Modifier.padding(horizontal = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EventIconDisplay(
                        icon               = event.icon,
                        contentDescription = event.title,
                        modifier           = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text  = "${event.title} (${MoneyFormatter.format(totalPaidForEvent)})",
                        style = labelM,
                        color = if (isSelected) AppColor.onPrimaryContainer else AppColor.text2,
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemListsTopBar(
    isDeleteMode: Boolean,
    onToggleDelete: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTopBar(
        titleText   = "Item Lists",
        onMenuClick = onMenuClick,
        modifier    = modifier,
        actions     = {
            Text(
                text     = if (isDeleteMode) "Done" else "Delete",
                style    = labelL,
                color    = AppColor.primary,
                modifier = Modifier.clickable { onToggleDelete() }.padding(Spacing.sm),
            )
        },
    )
}

@Composable
private fun ItemListContent(
    tasks: List<Task>,
    events: List<Event>,
    isDeleteMode: Boolean,
    onEditTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
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
                task          = task,
                event         = event,
                slot          = index % 6,
                isDeleteMode  = isDeleteMode,
                onEditClick   = { onEditTask(task.id) },
                onDeleteClick = { onDeleteTask(task.id) },
            )
        }
    }
}

/**
 * Item List Card matching specs:
 * Row 1: <Title> | <Total Amount> | <Edit/Delete Icon>
 * Row 2: <Event Icon (32dp, flush left)> | Total items X • Collected Y items
 *                                        | Progress Bar (10dp height, accent/success)
 * Row 3: <All items collected / Pending items Pill> | <Complete by Pill>
 */
@Composable
internal fun ItemListCard(
    task: Task,
    event: Event?,
    slot: Int,
    isDeleteMode: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val isDark = AppColor.isDark
    val palette = PaletteId.A
    val gradient: Brush = remember(slot, isDark) { gradientFor(slot, palette, isDark) }
    val iconColor = remember(slot, isDark) { gradientIconColor(slot, palette, isDark) }

    val totalItems = task.totalItems
    val remItems = task.remainingItems
    val progress = if (totalItems > 0) ((totalItems - remItems).toFloat() / totalItems.toFloat()).coerceIn(0f, 1f) else (if (task.completed) 1f else 0f)

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(Radius.card),
        color           = AppColor.surface.copy(alpha = 0f),
        shadowElevation = 1.dp,
        border          = BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else AppColor.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.card))
                .background(gradient)
                .padding(Spacing.md),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Row 1: [Title] (left) | [Total Amount] (middle) | <Edit/Delete Icon> (right)
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                Text(
                    text      = task.title,
                    style     = titleS,
                    color     = AppColor.text,
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis,
                    modifier  = Modifier.weight(1f),
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text     = "${MoneyFormatter.format(task.totalAmountPaise)} Paid",
                    style    = titleS,
                    color    = AppColor.text,
                    maxLines = 1,
                )
                Spacer(Modifier.width(Spacing.xs))
                if (isDeleteMode) {
                    Icon(
                        imageVector        = Icons.Rounded.Delete,
                        contentDescription = "Delete Item List",
                        tint               = AppColor.danger,
                        modifier           = Modifier
                            .size(20.dp)
                            .clickable { onDeleteClick() },
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Rounded.Edit,
                        contentDescription = "Edit Item List",
                        tint               = AppColor.primary,
                        modifier           = Modifier
                            .size(20.dp)
                            .clickable { onEditClick() },
                    )
                }
            }

            Spacer(Modifier.height(2.dp))

            // Row 2: [Task Icon 32dp flush left] | Total X item(s) • Collected Y item(s)
            //                                      Progress Bar (10dp)
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val taskCustomRes = task.icon.customRes
                val taskFallback = task.icon.fallback
                if (taskCustomRes != null) {
                    Image(
                        painter            = painterResource(taskCustomRes),
                        contentDescription = task.title,
                        modifier           = Modifier.size(32.dp),
                        contentScale       = ContentScale.Fit,
                    )
                } else {
                    Icon(
                        imageVector        = taskFallback,
                        contentDescription = task.title,
                        tint               = iconColor,
                        modifier           = Modifier.size(32.dp),
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val collectedItems = task.items.count { it.name.isNotBlank() && it.checked }
                    val totalUnit = if (totalItems <= 1) "item" else "items"
                    val collectedUnit = if (collectedItems <= 1) "item" else "items"
                    Text(
                        text     = "Total $totalItems $totalUnit   •   Collected $collectedItems $collectedUnit",
                        style    = bodyM,
                        color    = AppColor.text2,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(7.dp))
                    LinearProgressIndicator(
                        progress   = { progress },
                        modifier   = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(4.dp)),
                        color      = if (progress >= 1f) AppColor.success else AppColor.accent,
                        trackColor = AppColor.surface.copy(alpha = 0.4f),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Row 3 (Bottom Row): [Event Name] (left) | [All items collected? Pill] (middle) | [Complete by / Completed on Pill] (right)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // Left 1/3: Event Name
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text     = event?.title ?: "General",
                        style    = bodyM,
                        color    = AppColor.text2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Middle 1/3: Pending items / All collected Pill
                val isAllCollected = remItems == 0 || task.completed || progress >= 1f
                val collectedText = if (isAllCollected) {
                    if (totalItems <= 1) "All item collected" else "All items collected"
                } else {
                    if (remItems == 1) "1 item pending" else "$remItems items pending"
                }
                val collectedVariant = if (isAllCollected) PillVariant.Success else PillVariant.Warning
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Pill(text = collectedText, variant = collectedVariant)
                }

                // Right 1/3: Complete by / Completed on Pill
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    if (isAllCollected) {
                        val dateText = task.dueDate?.let { DateUtils.formatShort(it) } ?: DateUtils.formatShort(task.updatedAt)
                        Pill(
                            text    = "Completed on $dateText",
                            variant = PillVariant.Success,
                        )
                    } else if (task.dueDate != null) {
                        val daysLeft = DateUtils.daysUntil(task.dueDate)
                        val variant = if (daysLeft < 0L) PillVariant.Danger else PillVariant.Warning
                        Pill(
                            text    = "Complete by ${DateUtils.formatShort(task.dueDate)}",
                            variant = variant,
                        )
                    }
                }
            }
        }
    }
}
}
