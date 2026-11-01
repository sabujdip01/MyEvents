package sabuj.m.myevents.feature.invitees

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.model.Invitee

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
            ) {
                // Top bar
                InviteesTopBar(
                    isDeleteMode   = uiState.isDeleteMode,
                    onToggleDelete = { viewModel.toggleDeleteMode() },
                )

                // Sub-tabs (Invitees vs Gifts)
                MainSubTabRow(
                    activeTab   = uiState.mainTab,
                    onTabSelect = { viewModel.setMainTab(it) },
                )

                Spacer(Modifier.height(Spacing.sm))

                if (uiState.mainTab == InviteeMainTab.Invitees) {
                    // Event Tabs (Events requiring invitees)
                    val reqEvents = uiState.inviteeRequiredEvents
                    if (reqEvents.isNotEmpty()) {
                        InviteeEventTabs(
                            events          = reqEvents,
                            selectedEventId = uiState.selectedEventId,
                            onTabSelect     = { viewModel.selectEventTab(it) },
                        )
                        Spacer(Modifier.height(Spacing.sm))
                    }

                    // Search & Filter
                    InviteeSearchAndFilterBar(
                        searchQuery        = uiState.searchQuery,
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        activeFilter       = uiState.filter,
                        onFilterChange     = { viewModel.setFilter(it) },
                    )

                    Spacer(Modifier.height(Spacing.md))

                    // Invitees List / Table
                    val list = uiState.filteredInvitees
                    if (list.isEmpty()) {
                        EmptyListNotice(text = "No guests added yet")
                    } else {
                        InviteesTableContent(
                            invitees           = list,
                            isDeleteMode       = uiState.isDeleteMode,
                            selectedForDelete  = uiState.selectedForDelete,
                            onSelectForDelete  = { viewModel.toggleSelectForDelete(it) },
                            onEditInvitee      = { viewModel.showEditInviteeSheet(it) },
                            onToggleInvited    = { viewModel.toggleInvited(it) },
                            modifier           = Modifier.weight(1f),
                        )
                    }
                } else {
                    // Gifts Tab
                    if (uiState.events.isNotEmpty()) {
                        InviteeEventTabs(
                            events          = uiState.events,
                            selectedEventId = uiState.selectedEventId,
                            onTabSelect     = { viewModel.selectEventTab(it) },
                        )
                        Spacer(Modifier.height(Spacing.md))
                    }

                    val giftList = uiState.filteredGifts
                    if (giftList.isEmpty()) {
                        EmptyListNotice(text = "No gifts recorded yet")
                    } else {
                        GiftsTableContent(
                            gifts        = giftList,
                            invitees     = uiState.invitees,
                            onDeleteGift = { viewModel.deleteGift(it) },
                            modifier     = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {
                if (uiState.isDeleteMode) viewModel.deleteSelectedInvitees()
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
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter),
        )
    }

    // Sheets
    when (val sheet = uiState.sheetState) {
        is InviteeSheetState.AddInvitee, is InviteeSheetState.EditInvitee -> AddEditInviteeSheet(
            sheetState             = sheet,
            events                 = uiState.events,
            onDismiss              = { viewModel.hideSheet() },
            onNameChange           = { name ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(name = name) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(name = name) }
            },
            onToggleEventSelection = { eventId ->
                if (sheet is InviteeSheetState.AddInvitee) {
                    val current = sheet.selectedEventIds.toMutableSet()
                    if (current.contains(eventId)) current.remove(eventId) else current.add(eventId)
                    viewModel.updateAddInviteeSheet { it.copy(selectedEventIds = current) }
                }
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
            onNoteChange           = { note ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(note = note) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(note = note) }
            },
            onSave                 = { viewModel.saveInvitee() },
        )
        is InviteeSheetState.AddGift -> AddGiftSheet(
            sheetState          = sheet,
            events              = uiState.events,
            invitees            = uiState.invitees,
            onDismiss           = { viewModel.hideSheet() },
            onGiverNameChange   = { name -> viewModel.updateAddGiftSheet { it.copy(giverName = name) } },
            onEventChange       = { eventId -> viewModel.updateAddGiftSheet { it.copy(eventId = eventId) } },
            onAmountChange      = { amt -> viewModel.updateAddGiftSheet { it.copy(amountPaise = amt) } },
            onDescriptionChange = { desc -> viewModel.updateAddGiftSheet { it.copy(description = desc) } },
            onInviteeChange     = { invId -> viewModel.updateAddGiftSheet { it.copy(inviteeId = invId) } },
            onSave              = { viewModel.saveGift() },
        )
        else -> Unit
    }
}

@Composable
private fun InviteesTopBar(
    isDeleteMode: Boolean,
    onToggleDelete: () -> Unit,
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(56.dp))
        Text(
            text     = "Invitees & Gifts",
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
private fun MainSubTabRow(
    activeTab: InviteeMainTab,
    onTabSelect: (InviteeMainTab) -> Unit,
) {
    TabRow(
        selectedTabIndex = activeTab.ordinal,
        containerColor   = AppColor.background,
        contentColor     = AppColor.primary,
        indicator        = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                color    = AppColor.primary,
            )
        },
        modifier = Modifier.padding(horizontal = Spacing.lg),
    ) {
        Tab(
            selected = activeTab == InviteeMainTab.Invitees,
            onClick  = { onTabSelect(InviteeMainTab.Invitees) },
            text     = { Text("Invitees List", style = labelL) },
        )
        Tab(
            selected = activeTab == InviteeMainTab.Gifts,
            onClick  = { onTabSelect(InviteeMainTab.Gifts) },
            text     = { Text("Gifts Received", style = labelL) },
        )
    }
}

@Composable
private fun InviteeEventTabs(
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
            placeholder    = { Text("Search relative or address...", style = bodyM, color = AppColor.text3) },
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
            InviteeFilterChipItem(
                label      = "All",
                isSelected = activeFilter == InviteeFilter.All,
                onClick    = { onFilterChange(InviteeFilter.All) },
            )
            InviteeFilterChipItem(
                label      = "Invited",
                isSelected = activeFilter == InviteeFilter.Invited,
                onClick    = { onFilterChange(InviteeFilter.Invited) },
            )
            InviteeFilterChipItem(
                label      = "Not invited",
                isSelected = activeFilter == InviteeFilter.NotInvited,
                onClick    = { onFilterChange(InviteeFilter.NotInvited) },
            )
        }
    }
}

@Composable
private fun InviteeFilterChipItem(
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
private fun EmptyListNotice(text: String) {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = text, style = titleL, color = AppColor.text2)
            Spacer(Modifier.height(Spacing.sm))
            Text(text = "Tap + to add", style = labelM, color = AppColor.text3)
        }
    }
}

@Composable
private fun InviteesTableContent(
    invitees: List<Invitee>,
    isDeleteMode: Boolean,
    selectedForDelete: Set<String>,
    onSelectForDelete: (String) -> Unit,
    onEditInvitee: (String) -> Unit,
    onToggleInvited: (Invitee) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalGuestsCount = invitees.sumOf { it.expectedCount }
    val totalFamiliesCount = invitees.size

    LazyColumn(
        modifier            = modifier.fillMaxWidth(),
        contentPadding      = PaddingValues(start = Spacing.lg, end = Spacing.lg, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
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
                Text("Guest / Family", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1.5f))
                Text("Expected", style = labelS, color = AppColor.text2, modifier = Modifier.weight(0.8f))
                Text("Address", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1.2f))
                Text("Invited", style = labelS, color = AppColor.text2, modifier = Modifier.weight(0.8f))
            }
        }

        items(invitees, key = { it.id }) { invitee ->
            InviteeRow(
                invitee             = invitee,
                isDeleteMode        = isDeleteMode,
                isSelectedForDelete = selectedForDelete.contains(invitee.id),
                onSelectForDelete   = { onSelectForDelete(invitee.id) },
                onEdit              = { onEditInvitee(invitee.id) },
                onToggleInvited     = { onToggleInvited(invitee) },
            )
        }

        item {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.chip))
                    .background(AppColor.primaryContainer)
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("TOTAL: $totalFamiliesCount Families", style = titleS, color = AppColor.primary, modifier = Modifier.weight(2f))
                Text("$totalGuestsCount Guests", style = titleS, color = AppColor.primary, modifier = Modifier.weight(2f))
            }
        }
    }
}

@Composable
private fun InviteeRow(
    invitee: Invitee,
    isDeleteMode: Boolean,
    isSelectedForDelete: Boolean,
    onSelectForDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleInvited: () -> Unit,
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
        }

        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = invitee.name, style = titleS, color = AppColor.text)
            if (invitee.note.isNotBlank()) {
                Text(text = invitee.note, style = bodyM, color = AppColor.text2)
            }
        }

        Text(
            text     = "${invitee.expectedCount} guests",
            style    = labelL,
            color    = AppColor.text,
            modifier = Modifier.weight(0.8f),
        )

        Text(
            text     = invitee.address.ifBlank { "—" },
            style    = bodyM,
            color    = AppColor.text2,
            modifier = Modifier.weight(1.2f),
        )

        Box(
            modifier = Modifier
                .weight(0.8f)
                .clickable { onToggleInvited() },
        ) {
            if (invitee.invited) {
                Pill("Invited", variant = PillVariant.Success)
            } else {
                Pill("Pending", variant = PillVariant.Warning)
            }
        }
    }
}

@Composable
private fun GiftsTableContent(
    gifts: List<Gift>,
    invitees: List<Invitee>,
    onDeleteGift: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalGiftAmountPaise = gifts.sumOf { it.amountPaise }

    LazyColumn(
        modifier            = modifier.fillMaxWidth(),
        contentPadding      = PaddingValues(start = Spacing.lg, end = Spacing.lg, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        item {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.chip))
                    .background(AppColor.surfaceVariant)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Giver", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1.2f))
                Text("Amount / Gift", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1.5f))
                Text("Linked Guest", style = labelS, color = AppColor.text2, modifier = Modifier.weight(1.2f))
                Spacer(Modifier.width(32.dp))
            }
        }

        items(gifts, key = { it.id }) { gift ->
            val linkedInvitee = invitees.find { it.id == gift.inviteeId }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.field))
                    .background(AppColor.surface)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = gift.giverName, style = titleS, color = AppColor.text, modifier = Modifier.weight(1.2f))

                Column(modifier = Modifier.weight(1.5f)) {
                    if (gift.amountPaise > 0) {
                        Text(text = MoneyFormatter.format(gift.amountPaise), style = labelL, color = AppColor.primary)
                    }
                    if (gift.description.isNotBlank()) {
                        Text(text = gift.description, style = bodyM, color = AppColor.text2)
                    }
                }

                Text(
                    text     = linkedInvitee?.name ?: "—",
                    style    = bodyM,
                    color    = AppColor.text2,
                    modifier = Modifier.weight(1.2f),
                )

                IconButton(onClick = { onDeleteGift(gift.id) }) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete gift", tint = AppColor.danger)
                }
            }
        }

        item {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.chip))
                    .background(AppColor.primaryContainer)
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("TOTAL GIFTS VALUE:", style = titleS, color = AppColor.primary, modifier = Modifier.weight(2f))
                Text(MoneyFormatter.format(totalGiftAmountPaise), style = titleS, color = AppColor.primary, modifier = Modifier.weight(2f))
            }
        }
    }
}
