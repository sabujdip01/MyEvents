package sabuj.m.myevents.notifications

import android.content.Context
import sabuj.m.myevents.core.util.NotificationHelper

/**
 * High-level API for posting app notifications.
 * Callers should use [Notifier] instead of [NotificationHelper] directly
 * so that notification IDs and channel routing are managed in one place.
 */
object Notifier {

    private var nextId = 1000

    /** Post an event-reminder notification. */
    fun eventReminder(context: Context, eventTitle: String, daysLeft: Int) {
        NotificationHelper.postNotification(
            context        = context,
            notificationId = nextId++,
            channelId      = Channels.REMINDERS,
            title          = "Upcoming: $eventTitle",
            message        = "$daysLeft day${if (daysLeft == 1) "" else "s"} to go!",
        )
    }

    /** Post a budget-alert notification. */
    fun budgetAlert(context: Context, eventTitle: String, percentUsed: Int) {
        NotificationHelper.postNotification(
            context        = context,
            notificationId = nextId++,
            channelId      = Channels.BUDGET,
            title          = "Budget Alert: $eventTitle",
            message        = "You have used $percentUsed% of your budget.",
        )
    }

    /** Post a payment-due notification. */
    fun paymentDue(context: Context, vendorName: String, amountRupees: Long) {
        NotificationHelper.postNotification(
            context        = context,
            notificationId = nextId++,
            channelId      = Channels.DUES,
            title          = "Payment Due: $vendorName",
            message        = "₹$amountRupees is pending.",
        )
    }
}

