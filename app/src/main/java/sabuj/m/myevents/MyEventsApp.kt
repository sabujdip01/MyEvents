package sabuj.m.myevents

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Triggers Hilt's code generation and sets up
 * the dependency graph for the entire app.
 */
@HiltAndroidApp
class MyEventsApp : Application()
