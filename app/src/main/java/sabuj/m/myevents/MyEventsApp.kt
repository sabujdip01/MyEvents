package sabuj.m.myevents

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import sabuj.m.myevents.core.util.NotificationHelper
import sabuj.m.myevents.data.worker.NotificationScheduler
import sabuj.m.myevents.data.worker.SyncWorker
import javax.inject.Inject

/**
 * Application entry point. Triggers Hilt's code generation and sets up
 * the dependency graph for the entire app.
 */
@HiltAndroidApp
class MyEventsApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        NotificationScheduler.schedulePeriodicReminders(this)
        SyncWorker.scheduleSync(this)
    }
}
