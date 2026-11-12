package sabuj.m.myevents.feature.invitees

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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
import sabuj.m.myevents.core.ui.AppButtonRow
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
                // Top bar: [🃏 extra cards with count] [Add]
                InviteesTopBar(
                    onMenuClick        = onMenuClick,
                    extraCards         = uiState.extraCards,
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
                        invitees               = list,
                        events                 = uiState.events,
                        totalGuestCount        = uiState.totalGuestCount,
                        totalInvitedGuestCount = uiState.totalInvitedGuestCount,
                        totalCardCount         = uiState.totalCardInvitations,
                        totalFamilies          = uiState.totalFamilies,
                        totalInvitedFamilies   = uiState.totalInvitedFamilies,
                        onEditInvitee          = { viewModel.showEditInviteeSheet(it.id) },
                        onLongPressInvitee     = { viewModel.requestDeleteSingleInvitee(it) },
                        modifier               = Modifier.weight(1f),
                    )
                }
            }
        }

        AppSnackbar(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter).padding(bottom = 88.dp),
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
                // Single-select: always replace with the tapped event
                viewModel.updateAddInviteeSheet { it.copy(selectedEventIds = setOf(id)) }
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
 * Guests top bar with Extra Cards icon (with count text below if > 0) and "Add" button.
 */
@Composable
private fun InviteesTopBar(
    onMenuClick: () -> Unit,
    extraCards: Int,
    onAddClick: () -> Unit,
    onExtraCardsClick: () -> Unit,
) {
    AppTopBar(
        titleText   = "Guests",
        onMenuClick = onMenuClick,
        actions     = {
            // Extra Cards icon — tap to open Extra Cards popup (with count text if > 0)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier            = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onExtraCardsClick)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Icon(
                    imageVector        = Icons.Rounded.MailOutline,
                    contentDescription = "Extra invitation cards",
                    tint               = AppColor.primary,
                    modifier           = Modifier.size(20.dp),
                )
                Text(
                    text  = if (extraCards > 0) "$extraCards" else "",
                    style = labelM.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = AppColor.primary,
                )
            }
            Spacer(Modifier.width(Spacing.xs))
            // Add text button
            Text(
                text     = "Add",
                style    = labelL,
                color    = AppColor.primary,
                modifier = Modifier
                    .clickable(onClick = onAddClick)
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
    totalInvitedGuestCount: Int,
    totalCardCount: Int,
    totalFamilies: Int,
    totalInvitedFamilies: Int,
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
            totalGuests         = totalGuestCount,
            totalInvitedGuests  = totalInvitedGuestCount,
            totalCards          = totalCardCount,
            totalFamilies       = totalFamilies,
            totalInvitedFamilies = totalInvitedFamilies,
            modifier            = Modifier.navigationBarsPadding().padding(bottom = 6.dp),
        )
    }
}

// ---------------------------------------------------------------------------
// Guest Row Card
// ---------------------------------------------------------------------------

/**
 * Single-line tabular guest row — Excel-style without cell borders.
 *
 * Columns (left to right, same widths as [GuestSummaryFooter]):
 *  1. 0.26f  Guest Name  — text, left-aligned, 4 dp start padding
 *  2. 0.22f  Event Name  — text, left-aligned
 *  3. 0.14f  Guest count — number, right-aligned, 20dp left padding inset
 *  4. 0.14f  Invite Via  — Call/Card icon, center-aligned (blank for None)
 *  5. 0.20f  Status pill — Invited (Success) or Pending (Warning), right-aligned
 *
 * Tap = edit; Long-press = delete dialog.
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
        modifier        = Modifier.fillMaxWidth(),
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
            // Col 1 — Guest Name (26%, left, 4dp start inset)
            Box(
                modifier         = Modifier.weight(0.26f).padding(start = 4.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text     = invitee.name,
                    style    = labelL,
                    color    = AppColor.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Col 2 — Event Name (22%, left)
            Box(
                modifier         = Modifier.weight(0.22f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text     = event?.title ?: "General",
                    style    = bodyM.copy(fontSize = 12.sp),
                    color    = AppColor.text2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Col 3 — Guest count (14%, right, moved 20dp left)
            Box(
                modifier         = Modifier.weight(0.14f).padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text     = "${invitee.expectedCount}",
                    style    = bodyM.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                    color    = AppColor.text,
                    maxLines = 1,
                )
            }

            // Col 4 — Invitation Via icon (14%, center): Call or Card icon; blank for None
            Box(
                modifier         = Modifier.weight(0.14f),
                contentAlignment = Alignment.Center,
            ) {
                when (invitee.invitationType) {
                    InvitationType.Call -> Icon(
                        imageVector        = Icons.Rounded.Call,
                        contentDescription = "Call",
                        tint               = AppColor.text2,
                        modifier           = Modifier.size(16.dp),
                    )
                    InvitationType.Card -> Icon(
                        imageVector        = Icons.Rounded.MailOutline,
                        contentDescription = "Card",
                        tint               = AppColor.text2,
                        modifier           = Modifier.size(16.dp),
                    )
                    InvitationType.None -> { /* blank cell */ }
                }
            }

            // Col 5 — Status pill (20%, right-aligned)
            Box(
                modifier         = Modifier.weight(0.20f),
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
 * Single-line Excel-style total row. Layout mirrors [GuestRowCard] column widths.
 *
 * Col1 (0.26f): "<Count of Rows> Families"                      under Guest name
 * Col2 (0.22f): "<Count of Rows where invited> invited families" under Event name
 * Col3 (0.14f): "<Sum of Guests> Guests"                       under Guest count (20dp left)
 * Col4 (0.14f): "<Count where invitation through = card> Cards" under Invitation Via icon
 * Col5 (0.20f): "<Sum of Guests where invited> Invited guests"   under Status pill (right-aligned)
 */
@Composable
private fun GuestSummaryFooter(
    totalGuests: Int,
    totalInvitedGuests: Int,
    totalCards: Int,
    totalFamilies: Int,
    totalInvitedFamilies: Int,
    modifier: Modifier = Modifier,
) {
    val valueStyle = bodyM.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    val color      = AppColor.onPrimaryContainer

    Surface(
        modifier = Modifier.fillMaxWidth().then(modifier),
        shape    = RoundedCornerShape(Radius.field),
        color    = AppColor.primaryContainer,
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xs, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Col 1 (0.22f): "<Count of Rows> Families" under Guest name
            Box(
                modifier         = Modifier.weight(0.22f).padding(start = 2.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text     = "$totalFamilies Families",
                    style    = valueStyle,
                    color    = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Col 2 (0.26f): "<Count of Rows where invited> invited families" under Event name
            Box(
                modifier         = Modifier.weight(0.26f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text     = "$totalInvitedFamilies Invited families",
                    style    = valueStyle,
                    color    = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Col 3 (0.15f): "<Sum of Guests> Guests" under Guest count (20dp left)
            Box(
                modifier         = Modifier.weight(0.15f).padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text     = "$totalGuests Guests",
                    style    = valueStyle,
                    color    = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Col 4 (0.13f): "<Count where invitation through = card> Cards" under Invitation Via icon
            Box(
                modifier         = Modifier.weight(0.13f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text     = "$totalCards Cards",
                    style    = valueStyle,
                    color    = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Col 5 (0.24f): "<Sum of Guests where invited> Invited guests" under Status pill
            Box(
                modifier         = Modifier.weight(0.24f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text     = "$totalInvitedGuests Invited guests",
                    style    = valueStyle,
                    color    = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}


// ---------------------------------------------------------------------------
// Extra Cards Dialog
// ---------------------------------------------------------------------------

/**
 * Popup to set the count of extra (non-tracked) physical invitation cards.
 * Design matches PaymentDialogFull: AlertDialog with content + AppButtonRow inside text block.
 */
@Composable
private fun ExtraCardsDialog(
    current: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
) {
    var count by remember(current) { mutableStateOf(current) }
    var countText by remember(current) { mutableStateOf(if (current == 0) "" else current.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = AppColor.surface,
        shape            = RoundedCornerShape(Radius.dialog),
        title = {
            Text(
                text  = "Extra invitation cards",
                style = titleL,
                color = AppColor.text,
            )
        },
        text = {
            Column {
                // Count input field
                OutlinedTextField(
                    value         = countText,
                    onValueChange = { text ->
                        countText = text
                        count     = text.toIntOrNull()?.coerceAtLeast(0) ?: 0
                    },
                    label         = { Text("Number of extra cards", style = bodyM) },
                    placeholder   = { Text("0", style = bodyM, color = AppColor.text2) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape         = RoundedCornerShape(Radius.field),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = AppColor.primary,
                        unfocusedBorderColor = AppColor.outline,
                        focusedLabelColor    = AppColor.primary,
                        unfocusedLabelColor  = AppColor.text2,
                        cursorColor          = AppColor.primary,
                    ),
                )

                Spacer(Modifier.height(12.dp))

                AppButtonRow(
                    positiveText    = "Save",
                    onPositive      = { onSave(count) },
                    negativeText    = "Cancel",
                    onNegative      = onDismiss,
                    positiveEnabled = true,
                )

                Spacer(Modifier.height(12.dp))
            }
        },
        confirmButton = {},
        dismissButton = {},
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

// ---------------------------------------------------------------------------
// Screen-level previews (rule 3: light + dark, filled + empty state)
// ---------------------------------------------------------------------------

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun GuestRowCardFilledPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            sabuj.m.myevents.core.preview.PreviewData.sampleInvitees.forEachIndexed { idx, inv ->
                GuestRowCard(
                    invitee     = inv,
                    event       = sabuj.m.myevents.core.preview.PreviewData.sampleEvents.firstOrNull(),
                    slot        = idx,
                    onTap       = {},
                    onLongPress = {},
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun GuestSummaryFooterFilledPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        GuestSummaryFooter(totalGuests = 185, totalInvitedGuests = 120, totalCards = 42, totalFamilies = 38, totalInvitedFamilies = 30)
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun GuestSummaryFooterEmptyPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        GuestSummaryFooter(totalGuests = 0, totalInvitedGuests = 0, totalCards = 0, totalFamilies = 0, totalInvitedFamilies = 0)
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun ExtraCardsDialogPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        ExtraCardsDialog(current = 10, onDismiss = {}, onSave = {})
    }
}
