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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sabuj.m.myevents.core.theme.AppColor
import sabuj.m.myevents.core.theme.Radius
import sabuj.m.myevents.core.theme.Spacing
import sabuj.m.myevents.core.theme.bodyM
import sabuj.m.myevents.core.theme.labelL
import sabuj.m.myevents.core.theme.labelM
import sabuj.m.myevents.core.theme.labelS
import sabuj.m.myevents.core.theme.titleL
import sabuj.m.myevents.core.theme.titleM
import sabuj.m.myevents.core.theme.titleS
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Expense

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                // Top bar
                ExpensesTopBar(
                    isDeleteMode   = uiState.isDeleteMode,
                    onToggleDelete = { viewModel.toggleDeleteMode() },
                )

                Spacer(Modifier.height(Spacing.sm))

                // Dynamic Top Event Tabs
                if (uiState.events.isNotEmpty()) {
                    EventTabsRow(
                        events          = uiState.events,
                        selectedEventId = uiState.selectedEventId,
                        onTabSelect     = { viewModel.selectTab(it) },
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }

                // Search & Filter Bar
                SearchAndFilterBar(
                    searchQuery        = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    activeFilter       = uiState.filter,
                    onFilterChange     = { viewModel.setFilter(it) },
                )

                Spacer(Modifier.height(Spacing.md))

                // Table & List
                val expensesList = uiState.filteredExpenses
                val bookingList = uiState.filteredBookings

                if (expensesList.isEmpty() && bookingList.isEmpty()) {
                    Box(
                        modifier         = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text  = "No expenses yet",
                                style = titleL,
                                color = AppColor.text2,
                            )
                            Spacer(Modifier.height(Spacing.sm))
                            Text(
                                text  = "Tap + to add your first expense",
                                style = labelM,
                                color = AppColor.text3,
                            )
                        }
                    }
                } else {
                    ExpensesTableContent(
                        expenses           = expensesList,
                        bookings           = bookingList,
                        isDeleteMode       = uiState.isDeleteMode,
                        selectedForDelete  = uiState.selectedForDelete,
                        onSelectForDelete  = { viewModel.toggleSelectForDelete(it) },
                        onEditExpense      = { viewModel.showEditSheet(it) },
                        onToggleAllReceived = { viewModel.toggleAllReceived(it) },
                        modifier           = Modifier.weight(1f),
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                if (uiState.isDeleteMode) viewModel.deleteSelectedExpenses()
                else viewModel.showAddSheet()
            },
            containerColor = if (uiState.isDeleteMode) AppColor.danger else AppColor.primary,
            contentColor   = AppColor.onPrimary,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(Spacing.lg),
        ) {
            if (uiState.isDeleteMode) {
                Icon(
                    imageVector        = Icons.Rounded.Delete,
                    contentDescription = "Delete selected",
                )
            } else {
                Icon(
                    imageVector        = Icons.Rounded.Add,
                    contentDescription = "Add expense",
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter),
        )
    }

    // Add / Edit sheet
    when (val sheet = uiState.sheetState) {
        is ExpenseSheetState.Add, is ExpenseSheetState.Edit -> AddEditExpenseSheet(
            sheetState             = sheet,
            events                 = uiState.events,
            onDismiss              = { viewModel.hideSheet() },
            onPurposeChange        = { purpose ->
                if (sheet is ExpenseSheetState.Add) viewModel.updateAddSheetField { it.copy(purpose = purpose) }
                else if (sheet is ExpenseSheetState.Edit) viewModel.updateEditSheetField { it.copy(purpose = purpose) }
            },
            onToggleEventSelection = { eventId ->
                if (sheet is ExpenseSheetState.Add) {
                    val current = sheet.selectedEventIds.toMutableSet()
                    if (current.contains(eventId)) current.remove(eventId) else current.add(eventId)
                    viewModel.updateAddSheetField { it.copy(selectedEventIds = current) }
                }
            },
            onTotalAmountChange    = { total ->
                if (sheet is ExpenseSheetState.Add) viewModel.updateAddSheetField { it.copy(totalAmountPaise = total) }
                else if (sheet is ExpenseSheetState.Edit) viewModel.updateEditSheetField { it.copy(totalAmountPaise = total) }
            },
            onAmountDueChange      = { due ->
                if (sheet is ExpenseSheetState.Add) viewModel.updateAddSheetField { it.copy(amountDuePaise = due) }
                else if (sheet is ExpenseSheetState.Edit) viewModel.updateEditSheetField { it.copy(amountDuePaise = due) }
            },
            onItemChange           = { index, text ->
                if (sheet is ExpenseSheetState.Add) {
                    val current = sheet.items.toMutableList()
                    if (index in current.indices) current[index] = text
                    if (index == current.lastIndex && text.isNotBlank()) current.add("")
                    viewModel.updateAddSheetField { it.copy(items = current) }
                } else if (sheet is ExpenseSheetState.Edit) {
                    val current = sheet.items.toMutableList()
                    if (index in current.indices) current[index] = text
                    if (index == current.lastIndex && text.isNotBlank()) current.add("")
                    viewModel.updateEditSheetField { it.copy(items = current) }
                }
            },
            onRemoveItem           = { index ->
                if (sheet is ExpenseSheetState.Add) {
                    val current = sheet.items.toMutableList()
                    if (index in current.indices && current.size > 1) current.removeAt(index)
                    viewModel.updateAddSheetField { it.copy(items = current) }
                } else if (sheet is ExpenseSheetState.Edit) {
                    val current = sheet.items.toMutableList()
                    if (index in current.indices && current.size > 1) current.removeAt(index)
                    viewModel.updateEditSheetField { it.copy(items = current) }
                }
            },
            onAllReceivedChange    = { received ->
                if (sheet is ExpenseSheetState.Add) viewModel.updateAddSheetField { it.copy(allReceived = received) }
                else if (sheet is ExpenseSheetState.Edit) viewModel.updateEditSheetField { it.copy(allReceived = received) }
            },
            onSave                 = { viewModel.saveExpense() },
        )
        else -> Unit
    }
}

@Composable
private fun ExpensesTopBar(
    isDeleteMode: Boolean,
    onToggleDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(56.dp))
        Text(
            text     = "Expenses",
            style    = titleL,
            color    = AppColor.text,
            modifier = Modifier.weight(1f),
        )
        Text(
            text     = if (isDeleteMode) "Done" else "Edit",
            style    = labelL,
            color    = AppColor.primary,
            modifier = Modifier
                .clickable { onToggleDelete() }
                .padding(Spacing.sm),
        )
    }
}

@Composable
private fun EventTabsRow(
    events: List<Event>,
    selectedEventId: String?,
    onTabSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier            = modifier,
        contentPadding      = PaddingValues(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            TabChip(
                label      = "All",
                isSelected = selectedEventId == null,
                onClick    = { onTabSelect(null) },
            )
        }
        itemsIndexed(events) { _, event ->
            TabChip(
                label      = event.title,
                isSelected = selectedEventId == event.id,
                onClick    = { onTabSelect(event.id) },
            )
        }
    }
}

@Composable
private fun TabChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape   = RoundedCornerShape(Radius.chip),
        color   = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
        modifier = modifier,
    ) {
        Text(
            text     = label,
            style    = labelL,
            color    = if (isSelected) AppColor.primary else AppColor.text2,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilter: ExpenseFilter,
    onFilterChange: (ExpenseFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier.padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        OutlinedTextField(
            value          = searchQuery,
            onValueChange  = onSearchQueryChange,
            placeholder    = { Text("Search expense or item...", style = bodyM, color = AppColor.text3) },
            leadingIcon    = { Icon(Icons.Rounded.Search, contentDescription = null, tint = AppColor.text2) },
            singleLine     = true,
            modifier       = Modifier.fillMaxWidth(),
            shape          = RoundedCornerShape(Radius.field),
            colors         = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AppColor.primary,
                unfocusedBorderColor = AppColor.outline,
                cursorColor          = AppColor.primary,
            ),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            FilterChipSimple(
                label      = "All",
                isSelected = activeFilter == ExpenseFilter.All,
                onClick    = { onFilterChange(ExpenseFilter.All) },
            )
            FilterChipSimple(
                label      = "Due pending",
                isSelected = activeFilter == ExpenseFilter.DuePending,
                onClick    = { onFilterChange(ExpenseFilter.DuePending) },
            )
            FilterChipSimple(
                label      = "Items pending",
                isSelected = activeFilter == ExpenseFilter.ItemsPending,
                onClick    = { onFilterChange(ExpenseFilter.ItemsPending) },
            )
        }
    }
}

@Composable
private fun FilterChipSimple(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape   = RoundedCornerShape(Radius.chip),
        color   = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant,
    ) {
        Text(
            text     = label,
            style    = labelM,
            color    = if (isSelected) AppColor.primary else AppColor.text2,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun ExpensesTableContent(
    expenses: List<Expense>,
    bookings: List<Booking>,
    isDeleteMode: Boolean,
    selectedForDelete: Set<String>,
    onSelectForDelete: (String) -> Unit,
    onEditExpense: (String) -> Unit,
    onToggleAllReceived: (Expense) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalExpenseAmountPaise = expenses.sumOf { it.amountPaise } + bookings.sumOf { it.totalAmountPaise }
    val totalExpenseDuePaise = expenses.sumOf { it.amountDuePaise } + bookings.sumOf { it.amountLeftPaise }

    LazyColumn(
        modifier            = modifier.fillMaxWidth(),
        contentPadding      = PaddingValues(start = Spacing.lg, end = Spacing.lg, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // Table Header
        item {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.chip))
                    .background(AppColor.surfaceVariant)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isDeleteMode) Spacer(Modifier.width(32.dp))
                Text("Purpose / Item", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1.5f))
                Text("Amount", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1f))
                Text("Due", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1f))
                Text("Status", style = labelS, color = AppColor.text2, modifier = Modifier.weight(0.8f))
            }
        }

        // Expense Rows
        items(expenses, key = { "exp-${it.id}" }) { expense ->
            ExpenseRow(
                expense             = expense,
                isDeleteMode        = isDeleteMode,
                isSelectedForDelete = selectedForDelete.contains(expense.id),
                onSelectForDelete   = { onSelectForDelete(expense.id) },
                onEdit              = { onEditExpense(expense.id) },
                onToggleAllReceived = { onToggleAllReceived(expense) },
            )
        }

        // Read-only Vendor Booking Rows
        items(bookings, key = { "bk-${it.id}" }) { booking ->
            BookingVendorRow(booking = booking)
        }

        // Table Footer
        item {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.chip))
                    .background(AppColor.primaryContainer)
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("TOTAL", style = titleS, color = AppColor.primary, modifier = Modifier.weight(1.5f))
                Text(MoneyFormatter.format(totalExpenseAmountPaise), style = titleS, color = AppColor.primary, modifier = Modifier.weight(1f))
                Text(MoneyFormatter.format(totalExpenseDuePaise), style = titleS, color = AppColor.primary, modifier = Modifier.weight(1f))
                Spacer(Modifier.weight(0.8f))
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    expense: Expense,
    isDeleteMode: Boolean,
    isSelectedForDelete: Boolean,
    onSelectForDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleAllReceived: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.field))
            .background(AppColor.surface)
            .clickable {
                if (isDeleteMode) onSelectForDelete() else onEdit()
            }
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
        }

        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = expense.purpose, style = titleS, color = AppColor.text)
            if (expense.items.isNotEmpty()) {
                Text(
                    text  = expense.items.joinToString(", "),
                    style = bodyM,
                    color = AppColor.text2,
                )
            }
        }

        Text(
            text     = MoneyFormatter.format(expense.amountPaise),
            style    = labelL,
            color    = AppColor.text,
            modifier = Modifier.weight(1f),
        )

        Text(
            text     = MoneyFormatter.format(expense.amountDuePaise),
            style    = labelM,
            color    = if (expense.amountDuePaise > 0) AppColor.danger else AppColor.text2,
            modifier = Modifier.weight(1f),
        )

        Box(
            modifier = Modifier
                .weight(0.8f)
                .clickable { onToggleAllReceived() },
        ) {
            if (expense.allReceived) {
                Pill("Done", variant = PillVariant.Success)
            } else {
                Pill("Pending", variant = PillVariant.Warning)
            }
        }
    }
}

@Composable
private fun BookingVendorRow(booking: Booking) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.field))
            .background(AppColor.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = booking.title, style = titleS, color = AppColor.text2)
            Text(text = "Vendor Booking (${booking.contactName})", style = labelS, color = AppColor.text3)
        }

        Text(
            text     = MoneyFormatter.format(booking.totalAmountPaise),
            style    = labelL,
            color    = AppColor.text2,
            modifier = Modifier.weight(1f),
        )

        Text(
            text     = MoneyFormatter.format(booking.amountLeftPaise),
            style    = labelM,
            color    = if (booking.amountLeftPaise > 0) AppColor.warning else AppColor.text3,
            modifier = Modifier.weight(1f),
        )

        Pill("Vendor", variant = PillVariant.Default)
    }
}
