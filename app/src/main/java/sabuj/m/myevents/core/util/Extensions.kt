package sabuj.m.myevents.core.util

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Miscellaneous Kotlin extension helpers shared across the app.
 * Add new extensions here rather than scattering them in feature packages.
 */

/** Convert paise (Long) to a readable rupee string, e.g. 100000L → "₹1,000". */
fun Long.formatRupees(): String = sabuj.m.myevents.core.util.MoneyFormatter.format(this)

/** Format a timestamp (ms) as "d MMM yyyy". */
fun Long.toDisplayDate(locale: Locale = Locale.getDefault()): String {
    val sdf = SimpleDateFormat("d MMM yyyy", locale)
    return sdf.format(Date(this))
}

/** Clamp a value between [min] and [max]. */
fun Float.clamp(min: Float, max: Float): Float = coerceIn(min, max)

/** Convert dp to pixels using the display metrics. */
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

