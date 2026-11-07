package sabuj.m.myevents.domain.model

import java.time.LocalDate

/**
 * Derived summary used by the Home screen dashboard.
 * All amounts in paise. Counts are raw numbers.
 * Never stored — computed by [ObserveHomeSummaryUseCase].
 */
data class HomeSummary(
    val events: List<EventSummary> = emptyList(),
    /** Total budget across all events in paise. */
    val totalBudgetPaise: Long = 0L,
    /** Total spent (booking payments + expenses paid) in paise. */
    val totalSpentPaise: Long = 0L,
    /** Total pending dues (booking leftover + expense dues) in paise. */
    val totalDuesPaise: Long = 0L,
    /** Sum of expected invitee counts. */
    val totalGuests: Int = 0,
    val invitedGuests: Int = 0,
    /** Number of invitee rows (families). */
    val totalFamilies: Int = 0,
    val invitedFamilies: Int = 0,
    /** Number of expense/checklist items still pending. */
    val itemsPending: Int = 0,
    /** Bookings completed vs total. */
    val bookingsDone: Int = 0,
    val bookingsTotal: Int = 0,
    /** Booking amounts in paise. */
    val bookingAmountPaid: Long = 0L,
    val bookingAmountTotal: Long = 0L,
)

/** Per-event mini-summary for the Home upcoming-events card. */
data class EventSummary(
    val event: Event,
    val bookingCount: Int = 0,
    val inviteeCount: Int = 0,
) {
    val id: String get() = event.id
    val title: String get() = event.title
    val date: LocalDate get() = event.date
    val icon: sabuj.m.myevents.core.icons.EventIcon get() = event.icon
}

