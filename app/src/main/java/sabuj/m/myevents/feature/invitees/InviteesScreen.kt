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
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.MailOutline
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
                        invitees              = list,
                        events                = uiState.events,
                        totalGuestCount       = uiState.totalGuestCount,
                        totalInvitedGuestCount = uiState.totalInvitedGuestCount,
                        totalCardCount        = uiState.totalCardInvitations,
                        totalFamilies         = uiState.totalFamilies,
                        onEditInvitee         = { viewModel.showEditInviteeSheet(it.id) },
                        onLongPressInvitee    = { viewModel.requestDeleteSingleInvitee(it) },
                        modifier              = Modifier.weight(1f),
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
    totalInvitedGuestCount: Int,
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
            totalGuests        = totalGuestCount,
            totalInvitedGuests = totalInvitedGuestCount,
            totalCards         = totalCardCount,
            totalFamilies      = totalFamilies,
            modifier           = Modifier.navigationBarsPadding().padding(bottom = 6.dp),
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
 *  3. 0.14f  Guest count — number, right-aligned
 *  4. 0.14f  Invite Via  — Call/Card icon, center-aligned (blank for None)
 *  5. 0.20f  Status pill — Invited (Success) or Pending (Warning), center-aligned
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

            // Col 3 — Guest count (14%, right)
            Box(
                modifier         = Modifier.weight(0.14f),
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

            // Col 5 — Status pill (20%, center)
            Box(
                modifier         = Modifier.weight(0.20f),
                contentAlignment = Alignment.Center,
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
 * Sticky total row — same column widths as [GuestRowCard], Excel-style.
 *
 * Col 1 (0.26f) "Total"            — left, under Guest Name
 * Col 2 (0.22f) "N Families"       — left, under Event Name
 * Col 3 (0.14f) "N Guests"         — right, under Guest count
 * Col 4 (0.14f) "N Cards"          — right, under Invite Via icon
 * Col 5 (0.20f) "N Guests"         — center, under Status pill (invited guests only)
 */
@Composable
private fun GuestSummaryFooter(
    totalGuests: Int,
    totalInvitedGuests: Int,
    totalCards: Int,
    totalFamilies: Int,
    modifier: Modifier = Modifier,
) {
    val labelStyle = bodyM.copy(fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
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
            // Col 1 — "Total" (left, 4dp inset matching card name column)
            Box(
                modifier         = Modifier.weight(0.26f).padding(start = 4.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text     = "Total",
                    style    = labelStyle.copy(fontWeight = FontWeight.Bold),
                    color    = color,
                    maxLines = 1,
                )
            }

            // Col 2 — "N Families" (left, under Event Name)
            Box(
                modifier         = Modifier.weight(0.22f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text     = "$totalFamilies Families",
                    style    = labelStyle,
                    color    = color,
                    maxLines = 1,
                )
            }

            // Col 3 — "N Guests" (right, under Guest count)
            Box(
                modifier         = Modifier.weight(0.14f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text     = "$totalGuests Guests",
                    style    = labelStyle,
                    color    = color,
                    maxLines = 1,
                )
            }

            // Col 4 — "N Cards" (right, under Invite Via icon)
            Box(
                modifier         = Modifier.weight(0.14f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Text(
                    text     = "$totalCards Cards",
                    style    = labelStyle,
                    color    = color,
                    maxLines = 1,
                )
            }

            // Col 5 — "N Guests" invited (center, under Status pill)
            Box(
                modifier         = Modifier.weight(0.20f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text      = "$totalInvitedGuests Guests",
                    style     = labelStyle,
                    color     = color,
                    maxLines  = 1,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
        GuestSummaryFooter(totalGuests = 185, totalInvitedGuests = 120, totalCards = 42, totalFamilies = 38)
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun GuestSummaryFooterEmptyPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        GuestSummaryFooter(totalGuests = 0, totalInvitedGuests = 0, totalCards = 0, totalFamilies = 0)
    }
}

@sabuj.m.myevents.core.preview.LightDarkPreviews
@Composable
private fun ExtraCardsDialogPreview() {
    sabuj.m.myevents.core.theme.AppTheme {
        ExtraCardsDialog(current = 10, onDismiss = {}, onSave = {})
    }
}
