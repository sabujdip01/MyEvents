package sabuj.m.myevents.feature.home

import sabuj.m.myevents.domain.model.EventSummary

/**
 * UI state for the Home screen dashboard cards.
 */
data class HomeUiState(
    val upcomingEvents: List<EventSummary> = emptyList(),
    val bookingsDone: Int    = 0,
    val bookingsTotal: Int   = 0,
    val bookingAmountPaid: Long  = 0L,  // paise
    val bookingAmountTotal: Long = 0L,  // paise
    val budgetSpentPaise: Long   = 0L,
    val budgetTotalPaise: Long   = 0L,
    val pendingDuesPaise: Long   = 0L,
    val totalGuests: Int         = 0,
    val invitedGuests: Int       = 0,
    val totalFamilies: Int       = 0,
    val invitedFamilies: Int     = 0,
    /** Total invitation cards = Card-type invitees + extra cards from popup. */
    val invitationCards: Int     = 0,
    val itemsPending: Int        = 0,
)
