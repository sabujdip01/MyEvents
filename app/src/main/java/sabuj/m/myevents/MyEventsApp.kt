package sabuj.m.myevents

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import sabuj.m.myevents.core.util.NotificationHelper
import sabuj.m.myevents.data.worker.NotificationScheduler

/**
 * Application entry point. Triggers Hilt's code generation and sets up
 * the dependency graph for the entire app.
 */
@HiltAndroidApp
class MyEventsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        NotificationScheduler.schedulePeriodicReminders(this)
    }
}
