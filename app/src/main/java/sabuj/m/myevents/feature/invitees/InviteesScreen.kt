package sabuj.m.myevents.feature.invitees

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
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
import sabuj.m.myevents.core.ui.AppButton
import sabuj.m.myevents.core.ui.AppButtonText
import sabuj.m.myevents.core.ui.AppDeleteDialog
import sabuj.m.myevents.core.ui.AppSnackbar
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.InvitationType

@Composable
fun InviteesScreen(
    onMenuClick: () -> Unit,
    viewModel: InviteesViewModel = hiltViewModel(),
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
                // Top bar — "Add" tap to add, long-press for Extra Cards
                InviteesTopBar(
                    onMenuClick        = onMenuClick,
                    onAddClick         = { viewModel.showAddSheet() },
                    onExtraCardsClick  = { viewModel.showExtraCardsDialog() },
                )

                Spacer(Modifier.height(Spacing.sm))

                // Event filter tabs — show total invitees per event in brackets
                val reqEvents = uiState.inviteeRequiredEvents
                if (reqEvents.isNotEmpty()) {
                    InviteeEventTabs(
                        events          = reqEvents,
                        invitees        = uiState.invitees,
                        selectedEventId = uiState.selectedEventId,
                        onTabSelect     = { viewModel.selectEventTab(it) },
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }

                // Search & Filter
                InviteeSearchAndFilterBar(
                    searchQuery         = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    activeFilter        = uiState.filter,
                    onFilterChange      = { viewModel.setFilter(it) },
                )

                Spacer(Modifier.height(Spacing.md))

                // Guest list
                val list = uiState.filteredInvitees
                if (list.isEmpty()) {
                    EmptyListNotice(text = "No guests added yet")
                } else {
                    GuestsTableContent(
                        invitees          = list,
                        events            = uiState.events,
                        totalGuestCount   = uiState.totalGuestCount,
                        totalCardCount    = uiState.totalCardInvitations,
                        totalFamilies     = uiState.totalFamilies,
                        onEditInvitee     = { viewModel.showEditInviteeSheet(it.id) },
                        onLongPressInvitee = { viewModel.requestDeleteSingleInvitee(it) },
                        modifier          = Modifier.weight(1f),
                    )
                }
            }
        }

        AppSnackbar(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
        )
    }

    // Add / Edit Invitee Sheet
    when (val sheet = uiState.sheetState) {
        is InviteeSheetState.AddInvitee, is InviteeSheetState.EditInvitee -> AddEditInviteeSheet(
            sheetState             = sheet,
            events                 = uiState.events,
            onDismiss              = { viewModel.hideSheet() },
            onNameChange           = { name ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(name = name) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(name = name) }
            },
            onToggleEventSelection = { id ->
                val current = (sheet as? InviteeSheetState.AddInvitee)?.selectedEventIds ?: emptySet()
                val next = if (current.contains(id)) current - id else current + id
                viewModel.updateAddInviteeSheet { it.copy(selectedEventIds = next) }
            },
            onExpectedCountChange  = { count ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(expectedCount = count) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(expectedCount = count) }
            },
            onAddressChange        = { addr ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(address = addr) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(address = addr) }
            },
            onInvitedChange        = { inv ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(invited = inv) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(invited = inv) }
            },
            onInvitationTypeChange = { type ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(invitationType = type) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(invitationType = type) }
            },
            onNoteChange           = { note ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(note = note) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(note = note) }
            },
            onSave                 = { viewModel.saveInvitee() },
        )
        else -> Unit
    }

    // Single-invitee long-press delete dialog
    uiState.pendingDeleteInvitee?.let { inv ->
        AppDeleteDialog(
            title     = "Delete Guest?",
            message   = "\"${inv.name}\" will be permanently deleted.",
            onDismiss = { viewModel.cancelDeleteSingleInvitee() },
            onConfirm = { viewModel.confirmDeleteSingleInvitee() },
        )
    }

    // Extra Cards dialog
    if (uiState.showExtraCardsDialog) {
        ExtraCardsDialog(
            current   = uiState.extraCards,
            onDismiss = { viewModel.hideExtraCardsDialog() },
            onSave    = { viewModel.saveExtraCards(it) },
        )
    }
}

// ---------------------------------------------------------------------------
// Top Bar
// ---------------------------------------------------------------------------

/**
 * Guests top bar with "Add" text action (tap = add guest, long-press = extra cards popup).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InviteesTopBar(
    onMenuClick: () -> Unit,
    onAddClick: () -> Unit,
    onExtraCardsClick: () -> Unit,
) {
    AppTopBar(
        titleText   = "Guests",
        onMenuClick = onMenuClick,
        actions     = {
            Text(
                text     = "Add",
                style    = labelL,
                color    = AppColor.primary,
                modifier = Modifier
                    .combinedClickable(
                        onClick     = onAddClick,
                        onLongClick = onExtraCardsClick,
                    )
                    .padding(Spacing.sm),
            )
        },
    )
}

// ---------------------------------------------------------------------------
// Event Filter Tabs
// ---------------------------------------------------------------------------

/**
 * Horizontal tab row of events showing total invitee count per event in brackets.
 */
@Composable
private fun InviteeEventTabs(
    events: List<Event>,
    invitees: List<Invitee>,
    selectedEventId: String?,
    onTabSelect: (String?) -> Unit,
) {
    val totalAll = invitees.sumOf { it.expectedCount }
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
                    Text(
                        text  = "All ($totalAll)",
                        style = labelM,
                        color = if (isSelected) AppColor.primary else AppColor.text2,
                    )
                }
            }
        }

        itemsIndexed(events) { _, event ->
            val isSelected = selectedEventId == event.id
            val countForEvent = invitees.filter { it.eventId == event.id }.sumOf { it.expectedCount }
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
                    Text(
                        text  = "${event.title} ($countForEvent)",
                        style = labelM,
                        color = if (isSelected) AppColor.primary else AppColor.text2,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Search & Filter Bar
// ---------------------------------------------------------------------------

@Composable
private fun InviteeSearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilter: InviteeFilter,
    onFilterChange: (InviteeFilter) -> Unit,
) {
    Column(
        modifier            = Modifier.padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        OutlinedTextField(
            value          = searchQuery,
            onValueChange  = onSearchQueryChange,
            placeholder    = { Text("Search guest or address...", style = bodyM, color = AppColor.text3) },
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
                focusedBorderColor      = AppColor.primary,
                unfocusedBorderColor    = AppColor.outline,
                focusedContainerColor   = AppColor.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = AppColor.surfaceVariant.copy(alpha = 0.5f),
            ),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            InviteeFilter.entries.forEach { filter ->
                val isSelected = activeFilter == filter
                Surface(
                    onClick  = { onFilterChange(filter) },
                    shape    = RoundedCornerShape(Radius.chip),
                    color    = if (isSelected) AppColor.primaryContainer else AppColor.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.height(32.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = Spacing.sm)) {
                        Text(
                            text  = when (filter) {
                                InviteeFilter.All        -> "All"
                                InviteeFilter.Invited    -> "Invited"
                                InviteeFilter.NotInvited -> "Not invited"
                            },
                            style = labelM,
                            color = if (isSelected) AppColor.primary else AppColor.text2,
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Guests Table — list + sticky footer
// ---------------------------------------------------------------------------

/**
 * Column holding the scrollable guest cards and a sticky summary footer above the nav bar.
 */
@Composable
private fun GuestsTableContent(
    invitees: List<Invitee>,
    events: List<Event>,
    totalGuestCount: Int,
    totalCardCount: Int,
    totalFamilies: Int,
    onEditInvitee: (Invitee) -> Unit,
    onLongPressInvitee: (Invitee) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Scrollable list
        LazyColumn(
            modifier            = Modifier.weight(1f),
            contentPadding      = PaddingValues(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            itemsIndexed(invitees, key = { _, item -> item.id }) { index, item ->
                val event = events.find { it.id == item.eventId }
                GuestRowCard(
                    invitee           = item,
                    event             = event,
                    slot              = index % 6,
                    onTap             = { onEditInvitee(item) },
                    onLongPress       = { onLongPressInvitee(item) },
                )
            }
        }

        // Sticky footer above nav bar
        GuestSummaryFooter(
            totalGuests  = totalGuestCount,
            totalCards   = totalCardCount,
            totalFamilies = totalFamilies,
            modifier     = Modifier.navigationBarsPadding().padding(bottom = 6.dp),
        )
    }
}

// ---------------------------------------------------------------------------
// Guest Row Card
// ---------------------------------------------------------------------------

/**
 * Tabular guest card — same column-weight structure as ExpenseRowCard but without an icon column.
 *
 * Layout (weights sum to 1):
 * - 35% Title col: Guest name + 1dp gap + Event name
 * - 35% Guest count col: count value + "guest"/"guests" subtitle (right-aligned)
 * - 18% Invitation col: 1 if Card type, 0 otherwise + "Invitation" subtitle (right-aligned)
 * - 12% Pill col: Invited (Success) or Pending (Warning)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GuestRowCard(
    invitee: Invitee,
    event: Event?,
    slot: Int,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
) {
    val isDark = AppColor.isDark
    val palette = PaletteId.A
    val gradient: Brush = remember(slot, isDark) { gradientFor(slot, palette, isDark) }

    Surface(
        modifier        = Modifier.fillMaxWidth(), // full-width matching booking cards
        shape           = RoundedCornerShape(Radius.field),
        color           = AppColor.surface,
        shadowElevation = 1.dp,
        border          = androidx.compose.foundation.BorderStroke(
            0.8.dp,
            if (AppColor.isDark) AppColor.outlineVariant else AppColor.outlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .combinedClickable(onClick = onTap, onLongClick = onLongPress)
                .padding(horizontal = Spacing.xs, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 1. 35% Title col: guest name + gap + event name
            Column(
                modifier            = Modifier.weight(0.35f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text     = invitee.name,
                    style    = labelL,
                    color    = AppColor.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(1.dp)) // +20% gap
                Text(
                    text     = event?.title ?: "General",
                    style    = bodyM.copy(fontSize = 12.sp),
                    color    = AppColor.text2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // 2. 35% Guest count col: value + "guest"/"guests" label (right-aligned)
            Box(modifier = Modifier.weight(0.35f), contentAlignment = Alignment.CenterEnd) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text       = "${invitee.expectedCount}",
                        style      = bodyM.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                        color      = AppColor.text,
                        maxLines   = 1,
                    )
                    Spacer(Modifier.height(1.dp)) // +20% gap
                    Text(
                        text     = if (invitee.expectedCount == 1) "guest" else "guests",
                        style    = bodyM.copy(fontSize = 12.5.sp),
                        color    = AppColor.text2,
                        maxLines = 1,
                    )
                }
            }

            // 3. 18% Invitation col: 1 if Card, 0 otherwise + "Invitation" label (right-aligned)
            Box(modifier = Modifier.weight(0.18f), contentAlignment = Alignment.CenterEnd) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text     = if (invitee.invitationType == InvitationType.Card) "1" else "0",
                        style    = bodyM.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                        color    = AppColor.text,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(1.dp)) // +20% gap
                    Text(
                        text     = "Invitation",
                        style    = bodyM.copy(fontSize = 12.5.sp),
                        color    = AppColor.text2,
                        maxLines = 1,
                    )
                }
            }

            // 4. 12% Pill col: Invited (Success) or Pending (Warning)
            Box(
                modifier         = Modifier.weight(0.12f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Pill(
                    text    = if (invitee.invited) "Invited" else "Pending",
                    variant = if (invitee.invited) PillVariant.Success else PillVariant.Warning,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Guest Summary Footer
// ---------------------------------------------------------------------------

/**
 * Sticky footer matching the Expense SummaryFooter structure but with Guest columns:
 * - 35% → "Total" label (Row 1) | empty (Row 2)
 * - 35% → total guest count (Row 1) | "Guests" (Row 2)
 * - 18% → total card invitations (Row 1) | "Cards" (Row 2)
 * - 12% → total families (Row 1) | "Families" (Row 2)
 */
@Composable
private fun GuestSummaryFooter(
    totalGuests: Int,
    totalCards: Int,
    totalFamilies: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().then(modifier),
        shape    = RoundedCornerShape(Radius.field),
        color    = AppColor.primaryContainer,
    ) {
        Column(modifier = Modifier.padding(vertical = Spacing.sm)) {
            // Row 1: values
            Row(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 35% — "Total" label (aligns with title column)
                Text(
                    text       = "Total",
                    style      = labelL,
                    fontWeight = FontWeight.Bold,
                    color      = AppColor.onPrimaryContainer,
                    modifier   = Modifier.weight(0.35f),
                )
                // 35% — total guest count (right-aligned, matches guest col)
                Box(Modifier.weight(0.35f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text       = "$totalGuests",
                        style      = labelL,
                        fontWeight = FontWeight.Bold,
                        color      = AppColor.onPrimaryContainer,
                        maxLines   = 1,
                    )
                }
                // 18% — total card invitations (right-aligned, matches invitation col)
                Box(Modifier.weight(0.18f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text     = "$totalCards",
                        style    = labelL,
                        color    = AppColor.onPrimaryContainer,
                        maxLines = 1,
                    )
                }
                // 12% — total families (right-aligned, matches pill col)
                Box(Modifier.weight(0.12f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text     = "$totalFamilies",
                        style    = labelL,
                        color    = AppColor.onPrimaryContainer,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(4.dp)) // +20% gap between value and label rows

            // Row 2: labels
            Row(
                modifier          = Modifier.fillMaxWidth().padding(horizontal = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 35% — empty (aligns with title column)
                Spacer(Modifier.weight(0.35f))
                // 35% — "Guests" label
                Box(Modifier.weight(0.35f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text     = "Guests",
                        style    = bodyM.copy(fontSize = 12.5.sp),
                        color    = AppColor.onPrimaryContainer,
                        maxLines = 1,
                    )
                }
                // 18% — "Cards" label
                Box(Modifier.weight(0.18f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text     = "Cards",
                        style    = bodyM.copy(fontSize = 12.5.sp),
                        color    = AppColor.onPrimaryContainer,
                        maxLines = 1,
                    )
                }
                // 12% — "Families" label
                Box(Modifier.weight(0.12f), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text     = "Families",
                        style    = bodyM.copy(fontSize = 12.5.sp),
                        color    = AppColor.onPrimaryContainer,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Extra Cards Dialog
// ---------------------------------------------------------------------------

/**
 * Popup to set the count of extra (non-tracked) physical invitation cards.
 * Shows a header, a +/- counter, and a Save button.
 */
@Composable
private fun ExtraCardsDialog(
    current: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
) {
    var count by remember(current) { mutableStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Extra Cards", style = labelL, fontWeight = FontWeight.Bold, color = AppColor.text)
        },
        text = {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Decrement button
                Surface(
                    onClick = { if (count > 0) count-- },
                    shape   = RoundedCornerShape(Radius.chip),
                    color   = AppColor.surfaceVariant,
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease", tint = AppColor.text)
                    }
                }

                Spacer(Modifier.width(Spacing.lg))

                Text(
                    text  = "$count",
                    style = labelL.copy(fontSize = 24.sp),
                    color = AppColor.text,
                )

                Spacer(Modifier.width(Spacing.lg))

                // Increment button
                Surface(
                    onClick = { count++ },
                    shape   = RoundedCornerShape(Radius.chip),
                    color   = AppColor.surfaceVariant,
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Add, contentDescription = "Increase", tint = AppColor.text)
                    }
                }
            }
        },
        shape             = RoundedCornerShape(Radius.dialog),
        containerColor    = AppColor.surface,
        confirmButton     = { AppButton(text = "Save", onClick = { onSave(count) }) },
        dismissButton     = { AppButtonText(text = "Cancel", onClick = onDismiss) },
    )
}

// ---------------------------------------------------------------------------
// Empty state
// ---------------------------------------------------------------------------

@Composable
private fun EmptyListNotice(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = titleL, color = AppColor.text2)
    }
}
