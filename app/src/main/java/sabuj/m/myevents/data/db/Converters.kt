package sabuj.m.myevents.data.db

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Room TypeConverters for types that Room cannot store natively.
 * Registered at the database level via @TypeConverters annotation.
 */
class Converters {

    /** [LocalDate] ↔ epoch-day Long. */
    @TypeConverter fun fromEpochDay(value: Long): LocalDate = LocalDate.ofEpochDay(value)

    @TypeConverter fun toEpochDay(date: LocalDate): Long = date.toEpochDay()

    /** `List<String>` ↔ delimited String for items list. */
    @TypeConverter fun fromStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }

    @TypeConverter fun toStringList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString("|||")
    }
}

