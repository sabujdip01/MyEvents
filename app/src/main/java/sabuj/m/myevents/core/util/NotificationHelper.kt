package sabuj.m.myevents.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import sabuj.m.myevents.MainActivity

/** Helper class for managing notification channels and posting reminder notifications. */
object NotificationHelper {

    const val CHANNEL_REMINDERS = "reminders_channel"
    const val CHANNEL_BUDGET    = "budget_channel"
    const val CHANNEL_DUES      = "dues_channel"

    /** Create notification channels for Android O (API 26) and above. */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channelReminders = NotificationChannel(
                CHANNEL_REMINDERS,
                "Event Reminders & Tasks",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = "Event countdowns and checklist due reminders" }

            val channelBudget = NotificationChannel(
                CHANNEL_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = "Alerts when spending reaches or exceeds budget limits" }

            val channelDues = NotificationChannel(
                CHANNEL_DUES,
                "Payment Dues",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = "Vendor booking and expense payment due reminders" }

            manager.createNotificationChannels(listOf(channelReminders, channelBudget, channelDues))
        }
    }

    /** Post a notification if permission is granted. */
    fun postNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // System fallback icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted or notification suppressed by OS
        }
    }
}
