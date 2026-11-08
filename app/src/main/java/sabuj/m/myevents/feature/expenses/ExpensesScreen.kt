package sabuj.m.myevents.feature.expenses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    ExpensesContent(
        uiState             = uiState,
        onMenuClick         = onMenuClick,
        onTabSelect         = { viewModel.selectTab(it) },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onFilterChange      = { viewModel.setFilter(it) },
        onSnackbarDismiss   = { viewModel.snackbarDismissed() },
    )
}

@Composable
fun ExpensesContent(
    uiState: ExpensesUiState,
    onMenuClick: () -> Unit,
    onTabSelect: (String?) -> Unit = {},
    onSearchQueryChange: (String) -> Unit = {},
    onFilterChange: (ExpenseCategory) -> Unit = {},
    onSnackbarDismiss: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        onSnackbarDismiss()
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
                ExpensesTopBar(onMenuClick = onMenuClick)

                Spacer(Modifier.height(Spacing.sm))

                // Event Tabs with Sum of amounts & Pill radius
                if (uiState.events.isNotEmpty()) {
                    ExpenseEventTabsRow(
                        events          = uiState.events,
                        displayExpenses = uiState.allDisplayExpenses,
                        selectedEventId = uiState.selectedEventId,
                        onTabSelect     = onTabSelect,
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }

                // Search & Filter Chips (All, Bookings, Item Lists, Other, Due, Paid)
                ExpenseSearchAndFilterBar(
                    searchQuery         = uiState.searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    activeFilter        = uiState.filter,
                    onFilterChange      = onFilterChange,
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
                        displayExpenses = list,
                        events          = uiState.events,
                        modifier        = Modifier.weight(1f),
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun ExpensesTopBar(onMenuClick: () -> Unit) {
    AppTopBar(
        titleText   = "Expenses",
        onMenuClick = onMenuClick,
    )
}

@Composable
private fun ExpenseEventTabsRow(
    events: List<Event>,
    displayExpenses: List<DisplayExpense>,
    selectedEventId: String?,
    onTabSelect: (String?) -> Unit,
) {
    val totalAll = displayExpenses.sumOf { it.totalAmountPaise }
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
                        text  = "All (${MoneyFormatter.format(totalAll)})",
                        style = labelM,
                        color = if (isSelected) AppColor.onPrimaryContainer else AppColor.text2,
                    )
                }
            }
        }

        itemsIndexed(events) { _, event ->
            val isSelected = selectedEventId == event.id
            val totalForEvent = displayExpenses.filter { it.eventId == event.id }.sumOf { it.totalAmountPaise }
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
                        text  = "${event.title} (${MoneyFormatter.format(totalForEvent)})",
                        style = labelM,
                        color = if (isSelected) AppColor.onPrimaryContainer else AppColor.text2,
                    )
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
                    ExpenseCategory.All      -> "All"
                    ExpenseCategory.Bookings -> "Bookings"
                    ExpenseCategory.ItemList -> "Item Lists"
                    ExpenseCategory.Due      -> "Due"
                    ExpenseCategory.Paid     -> "Paid"
                }
                Surface(
                    onClick  = { onFilterChange(filter) },
                    shape    = RoundedCornerShape(Radius.chip),
                    color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.height(32.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = Spacing.sm)) {
                        Text(
                            text  = label,
                            style = labelM,
                            color = if (isSelected) AppColor.primary else AppColor.text2,
                        )
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
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            itemsIndexed(displayExpenses, key = { _, item -> item.id }) { index, item ->
                val event = events.find { it.id == item.eventId }
                ExpenseRowCard(
                    expense = item,
                    event   = event,
                    slot    = index % 6,
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                SummaryFooter(
                    totalItemsCount  = totalItemsCount,
                    grandTotalPaise  = grandTotalPaise,
                    paidTotalPaise   = paidTotalPaise,
                    dueTotalPaise    = dueTotalPaise,
                    totalBudgetPaise = totalBudgetPaise,
                    isOverBudget     = isOverBudget,
                )
            }
        }
    }
}

@Composable
private fun SummaryFooter(
    totalItemsCount: Int,
    grandTotalPaise: Long,
    paidTotalPaise: Long,
    dueTotalPaise: Long,
    totalBudgetPaise: Long,
    isOverBudget: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md),
        shape    = RoundedCornerShape(Radius.field),
        color    = AppColor.primaryContainer,
    ) {
        Column(modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm).navigationBarsPadding()) {
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

/**
 * Tabular Expense Card Layout matching specifications:
 * - Same wide width as booking cards (`padding(horizontal = Spacing.md)`)
 * - 25% height reduction (`padding(vertical = 6.dp)`)
 * - 13% Icon Section: Category Icon (Bookings/Item Lists/Expenses)
 * - 25% Title Section: Expense Title <Booking / Item List> & Event Subtitle (Left aligned)
 * - 50% Amount Section: 3 Excel-like Tabular Columns (+15% larger amounts font, right aligned)
 * - 12% Status Pill Section: Due / Paid Pill (Only "Due" or "Paid", no "Pending")
 */
@Composable
private fun ExpenseRowCard(
    expense: DisplayExpense,
    event: Event?,
    slot: Int,
) {
    val isDark = AppColor.isDark
    val palette = PaletteId.A
    val gradient: Brush = remember(slot, isDark) { gradientFor(slot, palette, isDark) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md), // Same wide width as booking cards
        shape    = RoundedCornerShape(Radius.field),
        color    = AppColor.surface,
        shadowElevation = 1.dp,
        border   = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else AppColor.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(horizontal = Spacing.xs, vertical = 6.dp), // 25% height reduction
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 1. 13% Relative Width: Booking / Item List Category Icon
            Box(
                modifier         = Modifier.weight(0.13f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier        = Modifier.size(32.dp).clip(CircleShape).background(AppColor.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    val categoryIcon = when (expense.category) {
                        ExpenseCategory.Bookings -> sabuj.m.myevents.core.icons.AppIcon.Bookings.fallback
                        ExpenseCategory.ItemList -> sabuj.m.myevents.core.icons.AppIcon.Checklist.fallback
                        else -> sabuj.m.myevents.core.icons.AppIcon.Expenses.fallback
                    }
                    Icon(
                        imageVector        = categoryIcon,
                        contentDescription = null,
                        tint               = AppColor.primary,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }

            // 2. 25% Relative Width: Expense Title <Booking / Item List> & Event Name Subtitle (Left aligned)
            Column(
                modifier            = Modifier.weight(0.25f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text     = expense.purpose,
                    style    = labelL,
                    color    = AppColor.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text     = event?.title ?: "General",
                    style    = bodyM.copy(fontSize = 12.sp),
                    color    = AppColor.text2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // 3. 50% Relative Width: 3 Excel-like Tabular Columns (Right aligned with 15% increased amounts font & text as subtitle)
            Row(
                modifier          = Modifier.weight(0.50f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Col 1: Total Amount
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text      = MoneyFormatter.format(expense.totalAmountPaise),
                        style     = bodyM.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                        color     = AppColor.text,
                        maxLines  = 1,
                        textAlign = TextAlign.End,
                    )
                }

                // Col 2: Paid Amount with text (blank if 0, holding place)
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    if (expense.paidAmountPaise > 0L) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text      = MoneyFormatter.format(expense.paidAmountPaise),
                                style     = bodyM.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                color     = AppColor.primary,
                                maxLines  = 1,
                                textAlign = TextAlign.End,
                            )
                            Text(
                                text      = "paid",
                                style     = bodyM.copy(fontSize = 10.sp),
                                color     = AppColor.text2,
                                maxLines  = 1,
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                }

                // Col 3: Due Amount with text (blank if 0, holding place)
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    if (expense.dueAmountPaise > 0L) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text      = MoneyFormatter.format(expense.dueAmountPaise),
                                style     = bodyM.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                color     = AppColor.danger,
                                maxLines  = 1,
                                textAlign = TextAlign.End,
                            )
                            Text(
                                text      = "due",
                                style     = bodyM.copy(fontSize = 10.sp),
                                color     = AppColor.danger,
                                maxLines  = 1,
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                }
            }

            // 4. 12% Relative Width: Due / Paid Status Pill (Only Due or Paid, no Pending)
            Box(
                modifier         = Modifier.weight(0.12f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Pill(
                    text    = if (expense.dueAmountPaise <= 0L) "Paid" else "Due",
                    variant = if (expense.dueAmountPaise <= 0L) PillVariant.Success else PillVariant.Danger,
                )
            }
        }
    }
}
