package sabuj.m.myevents.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [MoneyFormatter].
 * Run with: ./gradlew test   (or from Android Studio)
 */
class MoneyFormatterTest {

    @Test
    fun `format zero paise`() {
        assertEquals("₹0", MoneyFormatter.format(0L))
    }

    @Test
    fun `format whole rupees with Indian grouping`() {
        // 1,50,000 in Indian format
        assertEquals("₹1,50,000", MoneyFormatter.format(15_000_000L))
    }

    @Test
    fun `format small amount`() {
        assertEquals("₹500", MoneyFormatter.format(50_000L))
    }

    @Test
    fun `formatWithPaise shows paise when non-zero`() {
        assertEquals("₹150.50", MoneyFormatter.formatWithPaise(15_050L))
    }

    @Test
    fun `formatWithPaise hides paise when zero`() {
        assertEquals("₹150", MoneyFormatter.formatWithPaise(15_000L))
    }

    @Test
    fun `parseToPaise valid input`() {
        assertEquals(50_000L, MoneyFormatter.parseToPaise("500"))
    }

    @Test
    fun `parseToPaise returns null for invalid input`() {
        assertNull(MoneyFormatter.parseToPaise("abc"))
    }

    @Test
    fun `parseToPaise strips commas`() {
        assertEquals(150_000_00L, MoneyFormatter.parseToPaise("1,50,000"))
    }
}
