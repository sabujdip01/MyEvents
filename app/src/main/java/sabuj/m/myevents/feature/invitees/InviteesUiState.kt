package sabuj.m.myevents.feature.invitees

import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.InvitationType

enum class InviteeMainTab { Invitees, Gifts }

enum class InviteeFilter { All, Invited, NotInvited }

sealed class InviteeSheetState {
    object Hidden : InviteeSheetState()

    data class AddInvitee(
        val name: String = "",
        val selectedEventIds: Set<String> = emptySet(),
        val expectedCount: Int = 1,
        val address: String = "",
        val invited: Boolean = false,
        val note: String = "",
        val invitationType: InvitationType = InvitationType.None,
        val nameError: String? = null,
        val eventsError: String? = null,
    ) : InviteeSheetState()

    data class EditInvitee(
        val inviteeId: String,
        val eventId: String,
        val name: String,
        val expectedCount: Int,
        val address: String,
        val invited: Boolean,
        val note: String,
        val invitationType: InvitationType = InvitationType.None,
        val nameError: String? = null,
    ) : InviteeSheetState()
}

/** UI state for Invitees & Gifts screen. */
data class InviteesUiState(
    val isLoading: Boolean = true,
    val mainTab: InviteeMainTab = InviteeMainTab.Invitees,
    val events: List<Event> = emptyList(),
    val selectedEventId: String? = null, // null = All
    val invitees: List<Invitee> = emptyList(),
    val gifts: List<Gift> = emptyList(),
    val searchQuery: String = "",
    val filter: InviteeFilter = InviteeFilter.All,
    val sheetState: InviteeSheetState = InviteeSheetState.Hidden,
    val isDeleteMode: Boolean = false,
    val selectedForDelete: Set<String> = emptySet(),
    val snackbarMessage: String? = null,
    /** Extra invitation cards ordered via the Extra Cards popup. */
    val extraCards: Int = 0,
    /** Whether the Extra Cards popup is visible. */
    val showExtraCardsDialog: Boolean = false,
    /** Invitee pending long-press single-delete confirmation. */
    val pendingDeleteInvitee: Invitee? = null,
) {
    /** Filtered events that have invitees required == true (for the Invitees top tab row). */
    val inviteeRequiredEvents: List<Event>
        get() = events.filter { it.inviteesRequired }

    /** Filtered list of invitees according to event, search query, and filter chips. */
    val filteredInvitees: List<Invitee>
        get() {
            var result = if (selectedEventId == null) invitees else invitees.filter { it.eventId == selectedEventId }

            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                result = result.filter { inv ->
                    inv.name.lowercase().contains(q) || inv.address.lowercase().contains(q)
                }
            }

            result = when (filter) {
                InviteeFilter.All        -> result
                InviteeFilter.Invited    -> result.filter { it.invited }
                InviteeFilter.NotInvited -> result.filter { !it.invited }
            }

            return result
        }

    /** Total guest count across all filtered invitees. */
    val totalGuestCount: Int get() = filteredInvitees.sumOf { it.expectedCount }

    /** Sum of expectedCount for invitees where invited = true. */
    val totalInvitedGuestCount: Int get() = filteredInvitees.filter { it.invited }.sumOf { it.expectedCount }

    /** Number of card-type invitations in filtered list (not counting extra cards). */
    val totalCardInvitations: Int get() = filteredInvitees.count { it.invitationType == InvitationType.Card }

    /** Number of unique family rows in filtered list. */
    val totalFamilies: Int get() = filteredInvitees.size

    /** Number of invited family rows in filtered list (invited = true). */
    val totalInvitedFamilies: Int get() = filteredInvitees.count { it.invited }

    /** Filtered list of gifts according to selected event. */
    val filteredGifts: List<Gift>
        get() = if (selectedEventId == null) gifts else gifts.filter { it.eventId == selectedEventId }
}
