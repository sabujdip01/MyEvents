package sabuj.m.myevents.data.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/** Utility to schedule background periodic reminder worker using WorkManager. */
object NotificationScheduler {

    private const val WORK_NAME = "MyEventsReminderWorker"

    fun schedulePeriodicReminders(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval     = 12,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
