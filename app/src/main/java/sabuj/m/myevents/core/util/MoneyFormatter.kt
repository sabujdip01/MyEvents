package sabuj.m.myevents.core.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Formats monetary values stored as paise (Long) into Indian-locale
 * rupee strings with the ₹ symbol and Indian grouping (e.g. ₹1,50,000).
 *
 * All money in the app is Long (paise). This is the **only** place
 * formatting happens — screens never format money themselves.
 */
object MoneyFormatter {

    private val indianLocale = Locale("en", "IN")

    /**
     * Formats [paise] as a full rupee string with ₹ symbol.
     * Example: 15000000L → "₹1,50,000"
     */
    fun format(paise: Long): String {
        val rupees = paise / 100L
        val nf = NumberFormat.getNumberInstance(indianLocale)
        return "₹${nf.format(rupees)}"
    }

    /**
     * Formats [paise] with paise shown when non-zero.
     * Example: 15050L → "₹150.50", 15000L → "₹150"
     */
    fun formatWithPaise(paise: Long): String {
        val rupees = paise / 100L
        val remainingPaise = paise % 100L
        val nf = NumberFormat.getNumberInstance(indianLocale)
        return if (remainingPaise == 0L) "₹${nf.format(rupees)}"
        else "₹${nf.format(rupees)}.${remainingPaise.toString().padStart(2, '0')}"
    }

    /**
     * Parses a plain rupee string (no ₹, no commas) into paise.
     * Returns null if the string is not a valid number.
     */
    fun parseToPaise(input: String): Long? {
        val trimmed = input.replace(",", "").trim()
        return trimmed.toLongOrNull()?.times(100L)
    }
}
