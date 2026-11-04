package sabuj.m.myevents.feature.expenses

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
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.icons.EventIconDisplay
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.PaletteId
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.gradientFor
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.AppDeleteDialog
import sabuj.m.myevents.core.ui.AppFab
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.Event

@Composable
fun ExpensesScreen(
    onMenuClick: () -> Unit,
    viewModel: ExpensesViewModel = hiltViewModel(),
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
                // Top bar
                ExpensesTopBar(
                    isDeleteMode   = uiState.isDeleteMode,
                    onToggleDelete = { viewModel.toggleDeleteMode() },
                    onMenuClick    = onMenuClick,
                )

                Spacer(Modifier.height(Spacing.sm))

                // Event Tabs
                if (uiState.events.isNotEmpty()) {
                    ExpenseEventTabsRow(
                        events          = uiState.events,
                        selectedEventId = uiState.selectedEventId,
                        onTabSelect     = { viewModel.selectTab(it) },
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }

                // Search & Filter Chips (G3, G4, G6)
                ExpenseSearchAndFilterBar(
                    searchQuery         = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    activeFilter        = uiState.filter,
                    onFilterChange      = { viewModel.setFilter(it) },
                )

                Spacer(Modifier.height(Spacing.md))

                // Expense List
                val list = uiState.filteredDisplayExpenses
                if (list.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No expenses found", style = titleL, color = AppColor.text2)
                    }
                } else {
                    ExpenseListContent(
                        displayExpenses   = list,
                        events            = uiState.events,
                        isDeleteMode      = uiState.isDeleteMode,
                        selectedForDelete = uiState.selectedForDelete,
                        onSelectForDelete = { viewModel.toggleSelectForDelete(it) },
                        onEditExpense     = { viewModel.showEditSheet(it) },
                        modifier          = Modifier.weight(1f),
                    )
                }
            }
        }

        // FAB (Items G1 & F2 — Universal AppFab with navigationBarsPadding)
        AppFab(
            onClick = {
                if (uiState.isDeleteMode) viewModel.deleteSelectedExpenses()
                else viewModel.showAddSheet()
            },
            icon               = if (uiState.isDeleteMode) Icons.Rounded.Delete else Icons.Rounded.Add,
            contentDescription = if (uiState.isDeleteMode) "Delete selected" else "Add expense",
            modifier           = Modifier.align(Alignment.BottomEnd),
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp),
        )
    }

    // Add / Edit Sheet
    when (val sheet = uiState.sheetState) {
        is ExpenseSheetState.Add, is ExpenseSheetState.Edit -> AddEditExpenseSheet(
            sheetState          = sheet,
            events              = uiState.events,
            onDismiss           = { viewModel.hideSheet() },
            onPurposeChange     = { purpose ->
                if (sheet is ExpenseSheetState.Add) viewModel.updateAddSheetField { it.copy(purpose = purpose) }
                else if (sheet is ExpenseSheetState.Edit) viewModel.updateEditSheetField { it.copy(purpose = purpose) }
            },
            onEventChange       = { eventId ->
                if (sheet is ExpenseSheetState.Add) viewModel.updateAddSheetField { it.copy(eventId = eventId) }
                else if (sheet is ExpenseSheetState.Edit) viewModel.updateEditSheetField { it.copy(eventId = eventId) }
            },
            onTotalAmountChange = { total ->
                if (sheet is ExpenseSheetState.Add) viewModel.updateAddSheetField { it.copy(totalAmountPaise = total) }
                else if (sheet is ExpenseSheetState.Edit) viewModel.updateEditSheetField { it.copy(totalAmountPaise = total) }
            },
            onSave              = { viewModel.saveExpense() },
        )
        else -> Unit
    }

    // Delete Confirmation Dialog
    val deleteExpenseId = uiState.deleteExpenseId
    if (deleteExpenseId != null) {
        AppDeleteDialog(
            title     = "Delete Expense?",
            message   = "This expense will be permanently deleted.",
            onDismiss = { viewModel.toggleDeleteMode() },
            onConfirm = { viewModel.deleteSelectedExpenses() },
        )
    }
}

@Composable
private fun ExpensesTopBar(
    isDeleteMode: Boolean,
    onToggleDelete: () -> Unit,
    onMenuClick: () -> Unit,
) {
    AppTopBar(
        titleText   = "Expenses",
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
private fun ExpenseEventTabsRow(
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
                    EventIconDisplay(
                        icon               = event.icon,
                        contentDescription = event.title,
                        modifier           = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    Text(event.title, style = labelM, color = if (isSelected) AppColor.primary else AppColor.text2)
                }
            }
        }
    }
}

@Composable
private fun ExpenseSearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilter: ExpenseCategory,
    onFilterChange: (ExpenseCategory) -> Unit,
) {
    Column(
        modifier            = Modifier.padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        OutlinedTextField(
            value          = searchQuery,
            onValueChange  = onSearchQueryChange,
            placeholder    = { Text("Search expense...", style = bodyM, color = AppColor.text3) },
            leadingIcon    = { Icon(Icons.Rounded.Search, null, tint = AppColor.text3) },
            trailingIcon   = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Rounded.Delete, null, tint = AppColor.text3)
                    }
                }
            },
            singleLine     = true,
            modifier       = Modifier.fillMaxWidth().height(48.dp),
            shape          = RoundedCornerShape(Radius.field),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColor.primary,
                unfocusedBorderColor = AppColor.outline,
                focusedContainerColor = AppColor.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = AppColor.surfaceVariant.copy(alpha = 0.5f),
            ),
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            itemsIndexed(ExpenseCategory.entries.toTypedArray()) { _, filter ->
                val isSelected = activeFilter == filter
                val label = when (filter) {
                    ExpenseCategory.All        -> "All"
                    ExpenseCategory.Vendor     -> "Vendor"
                    ExpenseCategory.ItemList   -> "Item Lists"
                    ExpenseCategory.Other      -> "Other"
                    ExpenseCategory.DuePending -> "Due Pending"
                }
                Surface(
                    onClick  = { onFilterChange(filter) },
                    shape    = RoundedCornerShape(Radius.chip),
                    color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.height(32.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = Spacing.sm)) {
                        Text(label, style = labelM, color = if (isSelected) AppColor.primary else AppColor.text2)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseListContent(
    displayExpenses: List<DisplayExpense>,
    events: List<Event>,
    isDeleteMode: Boolean,
    selectedForDelete: Set<String>,
    onSelectForDelete: (String) -> Unit,
    onEditExpense: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalItemsCount = displayExpenses.size
    val grandTotalPaise = displayExpenses.sumOf { it.totalAmountPaise }
    val paidTotalPaise  = displayExpenses.sumOf { it.paidAmountPaise }
    val dueTotalPaise   = displayExpenses.sumOf { it.dueAmountPaise }
    val totalBudgetPaise = events.sumOf { it.budgetPaise }
    val isOverBudget    = totalBudgetPaise > 0L && grandTotalPaise > totalBudgetPaise

    Column(modifier = modifier) {
        LazyColumn(
            modifier       = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            itemsIndexed(displayExpenses, key = { _, item -> item.id }) { index, item ->
                val event = events.find { it.id == item.eventId }
                ExpenseRowCard(
                    expense           = item,
                    event             = event,
                    slot              = index % 6,
                    isDeleteMode      = isDeleteMode,
                    isSelected        = selectedForDelete.contains(item.id),
                    onSelectForDelete = { onSelectForDelete(item.id) },
                    onEdit            = { if (!item.isReadOnly) onEditExpense(item.originalId) },
                )
            }
        }

        // Summary Footer (Items G5 & G8 — Item count, Paid, Due, Grand Total, Budget & Over budget flag)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color    = AppColor.primaryContainer,
        ) {
            Column(modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)) {
                if (isOverBudget) {
                    Text(
                        text     = "⚠️ Over Budget by ${MoneyFormatter.format(grandTotalPaise - totalBudgetPaise)}",
                        style    = titleS,
                        color    = AppColor.danger,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Items: $totalItemsCount", style = labelM, color = AppColor.onPrimaryContainer)
                    Text("Paid: ${MoneyFormatter.format(paidTotalPaise)}", style = labelM, color = AppColor.onPrimaryContainer)
                    Text("Due: ${MoneyFormatter.format(dueTotalPaise)}", style = labelM, color = AppColor.danger)
                }
                Spacer(Modifier.height(2.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Total: ${MoneyFormatter.format(grandTotalPaise)}", style = labelL, color = AppColor.onPrimaryContainer, fontWeight = FontWeight.Bold)
                    Text("Budget: ${MoneyFormatter.format(totalBudgetPaise)}", style = labelL, color = AppColor.onPrimaryContainer)
                }
            }
        }
    }
}

/**
 * Expense Card Layout matching Item G8 specification:
 * Row 1: <Purpose> (using slot gradient for different events)
 * Row 2: <Checkbox (visible in Delete/Edit mode)> <Event Name> <Total Amount> <Paid already> <Due> <Status>
 */
@Composable
private fun ExpenseRowCard(
    expense: DisplayExpense,
    event: Event?,
    slot: Int,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onSelectForDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val isDark = AppColor.isDark
    val palette = PaletteId.A
    val gradient: Brush = remember(slot, isDark) { gradientFor(slot, palette, isDark) }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
        shape    = RoundedCornerShape(Radius.field),
        color    = AppColor.surface,
        shadowElevation = 1.dp,
        border   = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else AppColor.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .clickable(enabled = !expense.isReadOnly) { onEdit() }
                .padding(Spacing.md),
        ) {
            // Row 1: <Purpose>
            Text(
                text     = expense.purpose,
                style    = titleS,
                color    = AppColor.text,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(Spacing.xs))

            // Row 2: <Checkbox (if delete mode)> <Event Name> <Total Amount> <Paid> <Due> <Status>
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                if (isDeleteMode && !expense.isReadOnly) {
                    Checkbox(
                        checked         = isSelected,
                        onCheckedChange = { onSelectForDelete() },
                        colors          = CheckboxDefaults.colors(checkedColor = AppColor.danger),
                    )
                }

                Text(
                    text     = event?.title ?: "General",
                    style    = bodyM,
                    color    = AppColor.text2,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text  = MoneyFormatter.format(expense.totalAmountPaise),
                    style = bodyM,
                    color = AppColor.text,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text  = "${MoneyFormatter.format(expense.paidAmountPaise)} paid",
                    style = bodyM,
                    color = AppColor.primary,
                )

                if (expense.dueAmountPaise > 0L) {
                    Text(
                        text  = "Due ${MoneyFormatter.format(expense.dueAmountPaise)}",
                        style = bodyM,
                        color = AppColor.danger,
                    )
                }

                Pill(
                    text    = expense.statusText,
                    variant = if (expense.dueAmountPaise <= 0L) PillVariant.Success else PillVariant.Warning,
                )
            }
        }
    }
}
