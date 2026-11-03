package sabuj.m.myevents.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager worker that evaluates notification rules and posts any pending
 * notifications (budget alerts, event reminders, payment dues).
 *
 * Enqueue via [NotificationWorker.enqueue].
 */
@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // TODO: query DB for events/expenses and call Notifier as appropriate
        return Result.success()
    }

    companion object {
        /** Enqueue a one-shot [NotificationWorker] run. */
        fun enqueue(workManager: WorkManager) {
            workManager.enqueue(OneTimeWorkRequestBuilder<NotificationWorker>().build())
        }
    }
}

