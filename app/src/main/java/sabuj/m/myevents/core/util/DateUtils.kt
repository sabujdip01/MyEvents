package sabuj.m.myevents.core.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Date utility helpers used across the app.
 * All dates are [java.time.LocalDate] — available from API 26 (safe on min 29).
 */
object DateUtils {

    private val displayFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val shortFormatter   = DateTimeFormatter.ofPattern("d MMM")
    private val monthFormatter   = DateTimeFormatter.ofPattern("MMMM yyyy")

    /** Number of days from today to [date]. Negative means past. */
    fun daysUntil(date: LocalDate): Long =
        ChronoUnit.DAYS.between(LocalDate.now(), date)

    /**
     * Human-readable "days left" label.
     *  > 0  → "X days left"
     *  = 0  → "Today"
     *  < 0  → "X days ago"
     */
    fun daysLeftLabel(date: LocalDate): String {
        val diff = daysUntil(date)
        return when {
            diff > 1L  -> "${diff} days left"
            diff == 1L -> "Tomorrow"
            diff == 0L -> "Today"
            diff == -1L-> "Yesterday"
            else       -> "${-diff} days ago"
        }
    }

    /** "15 Jan 2025" */
    fun formatDisplay(date: LocalDate): String = date.format(displayFormatter)

    /** "15 Jan" */
    fun formatShort(date: LocalDate): String = date.format(shortFormatter)

    /** "January 2025" */
    fun formatMonth(yearMonth: YearMonth): String = yearMonth.format(monthFormatter)

    /**
     * All dates in [yearMonth] as a list, with leading nulls so the first
     * day falls on the correct column (Mon=0 … Sun=6 grid).
     */
    fun calendarGrid(yearMonth: YearMonth): List<LocalDate?> {
        val first = yearMonth.atDay(1)
        // Monday = 1 in ISO, so offset = dayOfWeek.value - 1
        val offset = first.dayOfWeek.value - 1
        val days = (1..yearMonth.lengthOfMonth()).map { yearMonth.atDay(it) }
        return List(offset) { null } + days
    }
}
