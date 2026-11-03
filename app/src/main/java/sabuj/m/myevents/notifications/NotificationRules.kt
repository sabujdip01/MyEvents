package sabuj.m.myevents.notifications

/**
 * Business rules that decide when to fire a notification.
 * All notification trigger logic lives here so it can be unit-tested
 * independently of WorkManager and Android APIs.
 */
object NotificationRules {

    /** Number of days before an event to fire an upcoming-event reminder. */
    const val EVENT_REMINDER_DAYS_BEFORE = 3

    /** Budget usage fraction (0.0–1.0) at which to fire a budget-alert notification. */
    const val BUDGET_ALERT_THRESHOLD = 0.90f

    /**
     * Returns true if a budget-alert notification should be posted.
     * @param spentPaise    total amount spent in paise.
     * @param budgetPaise   total event budget in paise.
     */
    fun shouldAlertBudget(spentPaise: Long, budgetPaise: Long): Boolean {
        if (budgetPaise <= 0L) return false
        return spentPaise.toFloat() / budgetPaise.toFloat() >= BUDGET_ALERT_THRESHOLD
    }
}

