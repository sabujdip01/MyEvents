package sabuj.m.myevents.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

/**
 * Unit tests for [DateUtils].
 */
class DateUtilsTest {

    @Test
    fun `calendarGrid for November 2026 starts on Sunday with 0 offset`() {
        val november2026 = YearMonth.of(2026, 11)
        val grid = DateUtils.calendarGrid(november2026)

        // Nov 1, 2026 is Sunday, so offset for Sun=0 grid must be 0
        assertEquals(30, grid.size)
        assertEquals(LocalDate.of(2026, 11, 1), grid[0])
        assertEquals(LocalDate.of(2026, 11, 30), grid[29])
    }

    @Test
    fun `calendarGrid for October 2026 starts on Thursday with 4 offset`() {
        val october2026 = YearMonth.of(2026, 10)
        val grid = DateUtils.calendarGrid(october2026)

        // Oct 1, 2026 is Thursday (4th column, 0=Sun, 1=Mon, 2=Tue, 3=Wed, 4=Thu)
        assertEquals(4 + 31, grid.size)
        assertNull(grid[0])
        assertNull(grid[1])
        assertNull(grid[2])
        assertNull(grid[3])
        assertEquals(LocalDate.of(2026, 10, 1), grid[4])
    }
}
