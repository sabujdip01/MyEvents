package sabuj.m.myevents.feature.home

/**
 * UI state for the Home screen.
 * All values are zero/empty until Phase 6 wires them to real data.
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
    val totalFamilies: Int       = 0,
    val itemsPending: Int        = 0,
)

/** Minimal event data shown in the Upcoming Events card. */
data class EventSummary(
    val id: Long,
    val name: String,
    val date: java.time.LocalDate,
    val iconKey: String,  // EventIcon enum name
)
