package sabuj.m.myevents.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import sabuj.m.myevents.core.util.DateUtils
import sabuj.m.myevents.core.util.MoneyFormatter
import sabuj.m.myevents.core.util.NotificationHelper
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import sabuj.m.myevents.domain.repository.ExpenseRepository
import sabuj.m.myevents.domain.repository.TaskRepository
import java.time.LocalDate

/**
 * Background WorkManager worker that checks pending reminders and posts notifications.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val expenseRepo: ExpenseRepository,
    private val taskRepo: TaskRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.createNotificationChannels(appContext)

        val today = LocalDate.now()
        val events = eventRepo.observeAll().first()
        val bookings = bookingRepo.observeAll().first()
        val expenses = expenseRepo.observeAllExpenses().first()
        val tasks = taskRepo.observeAllTasks().first()

        // 1. Event Countdowns
        events.forEachIndexed { index, event ->
            val daysLeft = DateUtils.daysUntil(event.date)
            if (daysLeft in listOf(30L, 7L, 1L, 0L)) {
                val msg = if (daysLeft == 0L) "${event.title} is TODAY!" else "${event.title} is in $daysLeft days"
                NotificationHelper.postNotification(
                    context        = appContext,
                    notificationId = 1000 + index,
                    channelId      = NotificationHelper.CHANNEL_REMINDERS,
                    title          = "Upcoming Event",
                    message        = msg,
                )
            }
        }

        // 2. Booking Payment Dues
        bookings.filter { !it.isFullyPaid && it.nextDueDate != null }.forEachIndexed { index, booking ->
            val due = booking.nextDueDate!!
            if (!due.isAfter(today.plusDays(3))) {
                NotificationHelper.postNotification(
                    context        = appContext,
                    notificationId = 2000 + index,
                    channelId      = NotificationHelper.CHANNEL_DUES,
                    title          = "Booking Payment Due",
                    message        = "\"${booking.title}\" has a payment of ${MoneyFormatter.format(booking.amountLeftPaise)} due on ${DateUtils.formatShort(due)}",
                )
            }
        }

        // 3. Overdue Checklist Tasks
        tasks.filter { !it.completed && it.dueDate != null }.forEachIndexed { index, task ->
            if (!task.dueDate!!.isAfter(today)) {
                NotificationHelper.postNotification(
                    context        = appContext,
                    notificationId = 3000 + index,
                    channelId      = NotificationHelper.CHANNEL_REMINDERS,
                    title          = "Task Due",
                    message        = "Task \"${task.title}\" is due!",
                )
            }
        }

        // 4. Budget Alerts
        val totalBudget = events.sumOf { it.budgetPaise }
        if (totalBudget > 0) {
            val totalSpent = bookings.sumOf { it.paidPaise } + expenses.sumOf { (it.amountPaise - it.amountDuePaise).coerceAtLeast(0L) }
            val ratio = totalSpent.toDouble() / totalBudget.toDouble()
            if (ratio >= 1.0) {
                NotificationHelper.postNotification(
                    context        = appContext,
                    notificationId = 4000,
                    channelId      = NotificationHelper.CHANNEL_BUDGET,
                    title          = "Budget Alert",
                    message        = "Total spending (${MoneyFormatter.format(totalSpent)}) has EXCEEDED your budget (${MoneyFormatter.format(totalBudget)})!",
                )
            } else if (ratio >= 0.8) {
                NotificationHelper.postNotification(
                    context        = appContext,
                    notificationId = 4001,
                    channelId      = NotificationHelper.CHANNEL_BUDGET,
                    title          = "Budget Warning",
                    message        = "Spending has reached ${(ratio * 100).toInt()}% of your total budget.",
                )
            }
        }

        return Result.success()
    }
}
