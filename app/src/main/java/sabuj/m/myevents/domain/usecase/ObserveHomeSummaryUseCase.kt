package sabuj.m.myevents.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import sabuj.m.myevents.domain.model.Booking
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.EventSummary
import sabuj.m.myevents.domain.model.Expense
import sabuj.m.myevents.domain.model.Gift
import sabuj.m.myevents.domain.model.HomeSummary
import sabuj.m.myevents.domain.model.Invitee
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
 * from Events, Bookings, Expenses, Invitees, Gifts, and Tasks.
 */
class ObserveHomeSummaryUseCase @Inject constructor(
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val expenseRepo: ExpenseRepository,
    private val inviteeRepo: InviteeRepository,
    private val giftRepo: GiftRepository,
    private val taskRepo: TaskRepository,
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
        ) { flows ->
            val events = flows[0] as List<Event>
            val bookings = flows[1] as List<Booking>
            val expenses = flows[2] as List<Expense>
            val invitees = flows[3] as List<Invitee>
            val gifts = flows[4] as List<Gift>
            val tasks = flows[5] as List<Task>

            val totalBudget = events.sumOf { it.budgetPaise }
            val bookingPaid = bookings.sumOf { it.paidPaise }
            val bookingTotal = bookings.sumOf { it.totalAmountPaise }
            val bookingDues = bookings.sumOf { it.amountLeftPaise }

            val expensePaid = expenses.sumOf { (it.amountPaise - it.amountDuePaise).coerceAtLeast(0L) }
            val expenseDues = expenses.sumOf { it.amountDuePaise }

            val giftTotal = gifts.sumOf { it.amountPaise }

            val totalSpent = (bookingPaid + expensePaid - giftTotal).coerceAtLeast(0L)
            val totalDues = bookingDues + expenseDues

            val totalGuestsCount = invitees.sumOf { it.expectedCount }
            val invitedGuestsCount = invitees.filter { it.invited }.sumOf { it.expectedCount }
            val totalFamiliesCount = invitees.size
            val invitedFamiliesCount = invitees.count { it.invited }

            val pendingExpenseItems = expenses.count { !it.allReceived }
            val pendingChecklistItems = tasks.count { !it.completed }
            val totalPendingItems = pendingExpenseItems + pendingChecklistItems

            val completedBookings = bookings.count { it.isFullyPaid }
            val totalBookingsCount = bookings.size

            HomeSummary(
                events             = events.map { e -> EventSummary(id = e.id, title = e.title, date = e.date, icon = e.icon) },
                totalBudgetPaise   = totalBudget,
                totalSpentPaise    = totalSpent,
                totalDuesPaise     = totalDues,
                totalGuests        = totalGuestsCount,
                invitedGuests      = invitedGuestsCount,
                totalFamilies      = totalFamiliesCount,
                invitedFamilies    = invitedFamiliesCount,
                itemsPending       = totalPendingItems,
                bookingsDone       = completedBookings,
                bookingsTotal      = totalBookingsCount,
                bookingAmountPaid  = bookingPaid,
                bookingAmountTotal = bookingTotal,
            )
        }
}
