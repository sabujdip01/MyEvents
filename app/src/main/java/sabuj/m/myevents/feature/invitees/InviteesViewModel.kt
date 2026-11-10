package sabuj.m.myevents.feature.invitees

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
import sabuj.m.myevents.domain.model.InvitationType
import sabuj.m.myevents.domain.usecase.AddGiftUseCase
import sabuj.m.myevents.domain.usecase.AddInviteeUseCase
import sabuj.m.myevents.domain.usecase.DeleteGiftsUseCase
import sabuj.m.myevents.domain.usecase.DeleteInviteesUseCase
import sabuj.m.myevents.domain.usecase.EditInviteeUseCase
import sabuj.m.myevents.domain.usecase.GiftSaveResult
import sabuj.m.myevents.domain.usecase.InviteeSaveResult
import sabuj.m.myevents.domain.usecase.ObserveEventsUseCase
import sabuj.m.myevents.domain.usecase.ObserveGiftsUseCase
import sabuj.m.myevents.domain.usecase.ObserveInviteesUseCase
import javax.inject.Inject

@HiltViewModel
class InviteesViewModel @Inject constructor(
    private val observeEvents: ObserveEventsUseCase,
    private val observeInvitees: ObserveInviteesUseCase,
    private val observeGifts: ObserveGiftsUseCase,
    private val addInvitee: AddInviteeUseCase,
    private val editInvitee: EditInviteeUseCase,
    private val deleteInvitees: DeleteInviteesUseCase,
    private val addGift: AddGiftUseCase,
    private val deleteGifts: DeleteGiftsUseCase,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InviteesUiState())
    val uiState: StateFlow<InviteesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                observeEvents(),
                observeInvitees(),
                observeGifts(),
                settingsDataStore.extraCardsFlow,
            ) { events, invitees, gifts, extraCards ->
                _uiState.value.copy(
                    isLoading  = false,
                    events     = events,
                    invitees   = invitees,
                    gifts      = gifts,
                    extraCards = extraCards,
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun setMainTab(tab: InviteeMainTab) {
        _uiState.update { it.copy(mainTab = tab, selectedEventId = null) }
    }

    fun selectEventTab(eventId: String?) {
        _uiState.update { it.copy(selectedEventId = eventId) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilter(filter: InviteeFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun showAddSheet() {
        val currentTab = uiState.value.mainTab
        if (currentTab == InviteeMainTab.Invitees) {
            val preselected = uiState.value.selectedEventId?.let { setOf(it) } ?: emptySet()
            _uiState.update {
                it.copy(
                    sheetState = InviteeSheetState.AddInvitee(
                        selectedEventIds = preselected,
                    )
                )
            }
        } else {
            val defaultEvent = uiState.value.selectedEventId ?: uiState.value.events.firstOrNull()?.id ?: ""
            _uiState.update {
                it.copy(
                    sheetState = InviteeSheetState.AddGift(
                        eventId = defaultEvent,
                    )
                )
            }
        }
    }

    fun showEditInviteeSheet(inviteeId: String) {
        val inv = uiState.value.invitees.find { it.id == inviteeId } ?: return
        _uiState.update {
            it.copy(
                sheetState = InviteeSheetState.EditInvitee(
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
        _uiState.update { it.copy(sheetState = InviteeSheetState.Hidden) }
    }

    fun updateAddInviteeSheet(update: (InviteeSheetState.AddInvitee) -> InviteeSheetState.AddInvitee) {
        val current = _uiState.value.sheetState as? InviteeSheetState.AddInvitee ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun updateEditInviteeSheet(update: (InviteeSheetState.EditInvitee) -> InviteeSheetState.EditInvitee) {
        val current = _uiState.value.sheetState as? InviteeSheetState.EditInvitee ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun updateAddGiftSheet(update: (InviteeSheetState.AddGift) -> InviteeSheetState.AddGift) {
        val current = _uiState.value.sheetState as? InviteeSheetState.AddGift ?: return
        _uiState.update { it.copy(sheetState = update(current)) }
    }

    fun saveInvitee() {
        viewModelScope.launch {
            when (val sheet = _uiState.value.sheetState) {
                is InviteeSheetState.AddInvitee -> {
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
                        InviteeSaveResult.NameBlank   -> applyInviteeError(sheet, nameError = "Enter guest name")
                        InviteeSaveResult.EventsEmpty -> applyInviteeError(sheet, eventsError = "Select at least one event")
                    }
                }
                is InviteeSheetState.EditInvitee -> {
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
                        InviteeSaveResult.NameBlank -> applyInviteeError(sheet, nameError = "Enter guest name")
                        else                        -> Unit
                    }
                }
                else -> Unit
            }
        }
    }

    fun saveGift() {
        viewModelScope.launch {
            val sheet = _uiState.value.sheetState as? InviteeSheetState.AddGift ?: return@launch
            val result = addGift(
                giverName   = sheet.giverName,
                eventId     = sheet.eventId,
                amountPaise = sheet.amountPaise,
                description = sheet.description,
                inviteeId   = sheet.inviteeId,
            )
            when (result) {
                GiftSaveResult.Success    -> hideSheet()
                GiftSaveResult.GiverBlank -> applyGiftError(sheet, giverError = "Enter giver name")
                GiftSaveResult.EventBlank -> applyGiftError(sheet, eventError = "Select an event")
            }
        }
    }

    private fun applyInviteeError(
        sheet: InviteeSheetState,
        nameError: String? = null,
        eventsError: String? = null,
    ) {
        _uiState.update {
            it.copy(
                sheetState = when (sheet) {
                    is InviteeSheetState.AddInvitee  -> sheet.copy(nameError = nameError, eventsError = eventsError)
                    is InviteeSheetState.EditInvitee -> sheet.copy(nameError = nameError)
                    else                             -> sheet
                }
            )
        }
    }

    private fun applyGiftError(
        sheet: InviteeSheetState.AddGift,
        giverError: String? = null,
        eventError: String? = null,
    ) {
        _uiState.update {
            it.copy(
                sheetState = sheet.copy(giverError = giverError, eventError = eventError)
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

    fun deleteGift(giftId: String) {
        viewModelScope.launch {
            deleteGifts(giftId)
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
                    snackbarMessage   = "${ids.size} guests deleted",
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
        viewModelScope.launch {
            deleteInvitees(listOf(invitee.id))
            _uiState.update { it.copy(pendingDeleteInvitee = null, snackbarMessage = "Guest deleted") }
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
