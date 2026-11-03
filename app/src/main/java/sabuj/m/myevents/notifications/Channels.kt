package sabuj.m.myevents.notifications

import sabuj.m.myevents.core.util.NotificationHelper

/**
 * Centralised channel IDs for the notifications package.
 * Channel registration is done in [NotificationHelper.createNotificationChannels].
 * Constants here mirror those in [NotificationHelper] and are the single source
 * of truth for channel IDs used inside this package.
 */
object Channels {
    /** Reminders for event countdowns and task due dates. */
    const val REMINDERS = NotificationHelper.CHANNEL_REMINDERS

    /** Alerts when event spending nears or exceeds budget. */
    const val BUDGET = NotificationHelper.CHANNEL_BUDGET

    /** Reminders for pending vendor payments and expense dues. */
    const val DUES = NotificationHelper.CHANNEL_DUES
}

