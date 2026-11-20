package sabuj.m.myevents.feature.guests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sabuj.m.myevents.data.settings.SettingsDataStore
import sabuj.m.myevents.domain.usecase.AddInviteeUseCase
import sabuj.m.myevents.domain.usecase.DeleteInviteesUseCase
import sabuj.m.myevents.domain.usecase.EditInviteeUseCase
import sabuj.m.myevents.domain.usecase.InviteeSaveResult
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.ObserveInviteesUseCase
import javax.inject.Inject

@HiltViewModel
class GuestsViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
    private val observeInvitees: ObserveInviteesUseCase,
    private val addInvitee: AddInviteeUseCase,
    private val editInvitee: EditInviteeUseCase,
    private val deleteInvitees: DeleteInviteesUseCase,
    private val inviteeRepo: sabuj.m.myevents.domain.repository.InviteeRepository,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuestsUiState())
    val uiState: StateFlow<GuestsUiState> = _uiState.asStateFlow()

    private var lastDeletedInvitee: sabuj.m.myevents.domain.model.Invitee? = null

    init {
        viewModelScope.launch {
            combine(
                observeEvents(),
                observeInvitees(),
                settingsDataStore.extraCardsFlow,
            ) { events, invitees, extraCards ->
                _uiState.value.copy(
                    isLoading  = false,
                    events     = events,
                    invitees   = invitees,
                    extraCards = extraCards,
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun selectEventTab(eventId: String?) {
        _uiState.update { it.copy(selectedEventId = eventId) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilter(filter: GuestFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun showAddSheet() {
        val preselected = uiState.value.selectedEventId?.let { setOf(it) } ?: emptySet()
        _uiState.update {
            it.copy(
                sheetState = GuestSheetState.AddGuest(
                    selectedEventIds = preselected,
                )
            )
        }
    }

    fun showEditInviteeSheet(inviteeId: String) {
        val inv = uiState.value.invitees.find { it.id == inviteeId } ?: return
        _uiState.update {
            it.copy(
                sheetState = GuestSheetState.EditGuest(
                    inviteeId      = inv.id,
                    eventId        = inv.eventId,
                    name           = inv.name,
                    expectedCount  = inv.expectedCount,
                    address        = inv.address,
                    invited        = inv.invited,
                    note           = inv.note,
                    invitationType = inv.invitationType,
                )
            )
        }
    }

    fun hideSheet() {
        _uiState.update { it.copy(sheetState = GuestSheetState.Hidden) }
    }

    fun updateAddInviteeSheet(update: (GuestSheetState.AddGuest) -> GuestSheetState.AddGuest) {
        val current = _uiState.value.sheetState as? GuestSheetState.AddGuest ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun updateEditInviteeSheet(update: (GuestSheetState.EditGuest) -> GuestSheetState.EditGuest) {
        val current = _uiState.value.sheetState as? GuestSheetState.EditGuest ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun saveInvitee() {
        viewModelScope.launch {
            when (val sheet = _uiState.value.sheetState) {
                is GuestSheetState.AddGuest -> {
                    val result = addInvitee(
                        name           = sheet.name,
                        eventIds       = sheet.selectedEventIds.toList(),
                        expectedCount  = sheet.expectedCount,
                        address        = sheet.address,
                        invited        = sheet.invited,
                        note           = sheet.note,
                        invitationType = sheet.invitationType,
                    )
                    when (result) {
                        InviteeSaveResult.Success     -> hideSheet()
                        InviteeSaveResult.NameBlank   -> applyInviteeError(sheet, nameError = "Enter Guest Name")
                        InviteeSaveResult.EventsEmpty -> applyInviteeError(sheet, eventsError = "Select at least one Event")
                    }
                }
                is GuestSheetState.EditGuest -> {
                    val existing = uiState.value.invitees.find { it.id == sheet.inviteeId } ?: return@launch
                    val result = editInvitee(
                        existing       = existing,
                        name           = sheet.name,
                        expectedCount  = sheet.expectedCount,
                        address        = sheet.address,
                        invited        = sheet.invited,
                        note           = sheet.note,
                        invitationType = sheet.invitationType,
                    )
                    when (result) {
                        InviteeSaveResult.Success   -> hideSheet()
                        InviteeSaveResult.NameBlank -> applyInviteeError(sheet, nameError = "Enter Guest Name")
                        else                        -> Unit
                    }
                }
                else -> Unit
            }
        }
    }

    private fun applyInviteeError(
        sheet: GuestSheetState,
        nameError: String? = null,
        eventsError: String? = null,
    ) {
        _uiState.update {
            it.copy(
                sheetState = when (sheet) {
                    is GuestSheetState.AddGuest  -> sheet.copy(nameError = nameError, eventsError = eventsError)
                    is GuestSheetState.EditGuest -> sheet.copy(nameError = nameError)
                    else                         -> sheet
                }
            )
        }
    }

    fun toggleInvited(invitee: sabuj.m.myevents.domain.model.Invitee) {
        viewModelScope.launch {
            editInvitee(
                existing      = invitee,
                name          = invitee.name,
                expectedCount = invitee.expectedCount,
                address       = invitee.address,
                invited       = !invitee.invited,
                note          = invitee.note,
            )
        }
    }

    fun toggleDeleteMode() {
        _uiState.update {
            it.copy(
                isDeleteMode      = !it.isDeleteMode,
                selectedForDelete = emptySet(),
            )
        }
    }

    fun toggleSelectForDelete(inviteeId: String) {
        _uiState.update { state ->
            val set = state.selectedForDelete.toMutableSet()
            if (set.contains(inviteeId)) set.remove(inviteeId) else set.add(inviteeId)
            state.copy(selectedForDelete = set)
        }
    }

    fun deleteSelectedInvitees() {
        val ids = uiState.value.selectedForDelete.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            deleteInvitees(ids)
            _uiState.update {
                it.copy(
                    isDeleteMode      = false,
                    selectedForDelete = emptySet(),
                    snackbarMessage   = "${ids.size} Guests deleted",
                )
            }
        }
    }

    /** Shows the delete confirmation dialog for a single invitee (long-press). */
    fun requestDeleteSingleInvitee(invitee: sabuj.m.myevents.domain.model.Invitee) {
        _uiState.update { it.copy(pendingDeleteInvitee = invitee) }
    }

    /** Confirms and executes the pending single-invitee deletion. */
    fun confirmDeleteSingleInvitee() {
        val invitee = _uiState.value.pendingDeleteInvitee ?: return
        lastDeletedInvitee = invitee
        viewModelScope.launch {
            deleteInvitees(listOf(invitee.id))
            _uiState.update { it.copy(pendingDeleteInvitee = null, snackbarMessage = "\"${invitee.name}\" deleted") }
        }
    }

    /** Restores the last deleted guest upon tapping UNDO in the snackbar. */
    fun undoDelete() {
        val inv = lastDeletedInvitee ?: return
        lastDeletedInvitee = null
        viewModelScope.launch {
            inviteeRepo.upsertInvitee(inv.copy(deleted = false, updatedAt = System.currentTimeMillis()))
        }
    }

    /** Cancels the pending single-invitee deletion dialog. */
    fun cancelDeleteSingleInvitee() {
        _uiState.update { it.copy(pendingDeleteInvitee = null) }
    }

    // --- Extra Cards ---

    fun showExtraCardsDialog() {
        _uiState.update { it.copy(showExtraCardsDialog = true) }
    }

    fun hideExtraCardsDialog() {
        _uiState.update { it.copy(showExtraCardsDialog = false) }
    }

    fun saveExtraCards(count: Int) {
        viewModelScope.launch {
            settingsDataStore.setExtraCards(count)
            _uiState.update { it.copy(showExtraCardsDialog = false) }
        }
    }

    fun snackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
