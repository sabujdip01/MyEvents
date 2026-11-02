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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import sabuj.m.myevents.core.ui.AppFab
import sabuj.m.myevents.core.ui.AppTopBar
import sabuj.m.myevents.core.ui.Pill
import sabuj.m.myevents.core.ui.PillVariant
import sabuj.m.myevents.core.ui.ScreenScaffold
import sabuj.m.myevents.domain.model.Event
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

            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                InviteesTopBar(
                    isDeleteMode   = uiState.isDeleteMode,
                    onToggleDelete = { viewModel.toggleDeleteMode() },
                    onMenuClick    = onMenuClick,
                )

                Spacer(Modifier.height(Spacing.sm))

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
                    searchQuery         = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    activeFilter        = uiState.filter,
                    onFilterChange      = { viewModel.setFilter(it) },
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
                        onEditInvitee      = { viewModel.showEditInviteeSheet(it.id) },
                        onToggleInvited    = { viewModel.toggleInvited(it) },
                        modifier           = Modifier.weight(1f),
                    )
                }
            }
        }

        // FAB (Items H1 & F2 — Round AppFab with navigationBarsPadding)
        AppFab(
            onClick = {
                if (uiState.isDeleteMode) viewModel.deleteSelectedInvitees()
                else viewModel.showAddSheet()
            },
            icon               = if (uiState.isDeleteMode) Icons.Rounded.Delete else Icons.Rounded.Add,
            contentDescription = if (uiState.isDeleteMode) "Delete selected" else "Add guest",
            modifier           = Modifier.align(Alignment.BottomEnd),
        )

        SnackbarHost(
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
            onNoteChange           = { note ->
                if (sheet is InviteeSheetState.AddInvitee) viewModel.updateAddInviteeSheet { it.copy(note = note) }
                else if (sheet is InviteeSheetState.EditInvitee) viewModel.updateEditInviteeSheet { it.copy(note = note) }
            },
            onSave                 = { viewModel.saveInvitee() },
        )
        else -> Unit
    }
}

@Composable
private fun InviteesTopBar(
    isDeleteMode: Boolean,
    onToggleDelete: () -> Unit,
    onMenuClick: () -> Unit,
) {
    AppTopBar(
        titleText   = "Invitees",
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
private fun InviteeEventTabs(
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

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            InviteeFilter.values().forEach { filter ->
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

@Composable
private fun InviteesTableContent(
    invitees: List<Invitee>,
    isDeleteMode: Boolean,
    selectedForDelete: Set<String>,
    onSelectForDelete: (String) -> Unit,
    onEditInvitee: (Invitee) -> Unit,
    onToggleInvited: (Invitee) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalGuests = invitees.sumOf { it.expectedCount }

    Column(modifier = modifier) {
        LazyColumn(
            modifier       = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            itemsIndexed(invitees, key = { _, item -> item.id }) { _, item ->
                InviteeRowCard(
                    invitee           = item,
                    isDeleteMode      = isDeleteMode,
                    isSelected        = selectedForDelete.contains(item.id),
                    onSelectForDelete = { onSelectForDelete(item.id) },
                    onEdit            = { onEditInvitee(item) },
                    onToggleInvited   = { onToggleInvited(item) },
                )
            }
        }

        // Summary footer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color    = AppColor.primaryContainer,
        ) {
            Row(
                modifier          = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Total Families: ${invitees.size}", style = labelL, color = AppColor.onPrimaryContainer)
                Text("Total Guests: $totalGuests", style = labelL, color = AppColor.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun InviteeRowCard(
    invitee: Invitee,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onSelectForDelete: () -> Unit,
    onEdit: () -> Unit,
    onToggleInvited: () -> Unit,
) {
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
        Row(
            modifier          = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isDeleteMode) {
                Checkbox(
                    checked         = isSelected,
                    onCheckedChange = { onSelectForDelete() },
                    colors          = CheckboxDefaults.colors(checkedColor = AppColor.danger),
                )
                Spacer(Modifier.width(Spacing.sm))
            }

            Column(modifier = Modifier.weight(1f).clickable { onEdit() }) {
                Text(invitee.name, style = titleS, color = AppColor.text)
                if (invitee.address.isNotBlank()) {
                    Text(invitee.address, style = bodyM, color = AppColor.text2)
                }
            }

            Text("${invitee.expectedCount} guests", style = labelM, color = AppColor.text2)
            Spacer(Modifier.width(Spacing.sm))

            Pill(
                text    = if (invitee.invited) "Invited" else "Pending",
                variant = if (invitee.invited) PillVariant.Success else PillVariant.Warning,
                modifier = Modifier.clickable { onToggleInvited() },
            )
        }
    }
}

@Composable
private fun EmptyListNotice(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, style = titleL, color = AppColor.text2)
    }
}
