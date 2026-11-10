package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import sabuj.m.myevents.data.settings.SettingsDataStore
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.EventSummary
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.model.HomeSummary
import sabuj.m.myevents.domain.model.Invitee
import sabuj.m.myevents.domain.model.InvitationType
import sabuj.m.myevents.domain.model.Task
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import sabuj.m.myevents.domain.repository.ExpenseRepository
import sabuj.m.myevents.domain.repository.GiftRepository
import sabuj.m.myevents.domain.repository.InviteeRepository
import sabuj.m.myevents.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Derives the [HomeSummary] shown on the Home dashboard by combining live data
 * from Events, Bookings, Expenses, Invitees, Gifts, Tasks, and Settings (extra cards).
 */
class ObserveHomeSummaryUseCase @Inject constructor(
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val expenseRepo: ExpenseRepository,
    private val inviteeRepo: InviteeRepository,
    private val giftRepo: GiftRepository,
    private val taskRepo: TaskRepository,
    private val settingsDataStore: SettingsDataStore,
) {
    @Suppress("UNCHECKED_CAST")
    operator fun invoke(): Flow<HomeSummary> =
        combine(
            eventRepo.observeAll(),
            bookingRepo.observeAll(),
            expenseRepo.observeAllExpenses(),
            inviteeRepo.observeAllInvitees(),
            giftRepo.observeAllGifts(),
            taskRepo.observeAllTasks(),
            settingsDataStore.extraCardsFlow,
        ) { flows ->
            val events = flows[0] as List<Event>
            val bookings = flows[1] as List<Booking>
            val expenses = flows[2] as List<Expense>
            val invitees = flows[3] as List<Invitee>
            val gifts = flows[4] as List<Gift>
            val tasks = flows[5] as List<Task>
            val extraCards = flows[6] as Int

            val totalBudget = events.sumOf { it.budgetPaise }
            val bookingPaid = bookings.sumOf { it.paidPaise }
            val bookingTotal = bookings.sumOf { it.totalAmountPaise }
            val bookingDues = bookings.sumOf { it.amountLeftPaise }

            val taskTotal = tasks.sumOf { it.totalAmountPaise }
            val expenseDues = expenses.sumOf { it.amountDuePaise }

            val totalSpent = (bookingTotal + taskTotal).coerceAtLeast(0L)
            val totalDues = bookingDues + expenseDues

            val totalGuestsCount = invitees.sumOf { it.expectedCount }
            val invitedGuestsCount = invitees.filter { it.invited }.sumOf { it.expectedCount }
            val totalFamiliesCount = invitees.size
            val invitedFamiliesCount = invitees.count { it.invited }
            val cardInvitationsCount = invitees.count { it.invitationType == InvitationType.Card }
            val totalInvitationCards = cardInvitationsCount + extraCards

            val pendingExpenseItems = expenses.count { !it.allReceived }
            val pendingChecklistItems = tasks.count { !it.completed }
            val totalPendingItems = pendingExpenseItems + pendingChecklistItems

            val completedBookings = bookings.count { it.bookingMade }
            val totalBookingsCount = bookings.size

            HomeSummary(
                events             = events.map { e ->
                    val bCount = bookings.count { it.eventId == e.id }
                    val iCount = invitees.filter { it.eventId == e.id }.sumOf { maxOf(1, it.expectedCount) }
                    EventSummary(
                        event        = e,
                        bookingCount = bCount,
                        inviteeCount = iCount,
                    )
                },
                totalBudgetPaise   = totalBudget,
                totalSpentPaise    = totalSpent,
                totalDuesPaise     = totalDues,
                totalGuests        = totalGuestsCount,
                invitedGuests      = invitedGuestsCount,
                totalFamilies      = totalFamiliesCount,
                invitedFamilies    = cardInvitationsCount, // kept for legacy compat; Home shows invitationCards
                invitationCards    = totalInvitationCards,
                itemsPending       = totalPendingItems,
                bookingsDone       = completedBookings,
                bookingsTotal      = totalBookingsCount,
                bookingAmountPaid  = bookingPaid,
                bookingAmountTotal = bookingTotal,
            )
        }
}
