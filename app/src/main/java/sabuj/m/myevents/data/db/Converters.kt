package sabuj.m.myevents.data.db

import androidx.room.TypeConverter
import sabuj.m.myevents.domain.model.TaskItem
import java.time.LocalDate

/**
 * Room TypeConverters for types that Room cannot store natively.
 */
class Converters {

    @TypeConverter fun fromEpochDay(value: Long): LocalDate = LocalDate.ofEpochDay(value)

    @TypeConverter fun toEpochDay(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter fun fromStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }

    @TypeConverter fun toStringList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString("|||")
    }

    @TypeConverter fun fromTaskItemList(value: String?): List<TaskItem> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").mapNotNull { entry ->
            val parts = entry.split(":::")
            if (parts.size >= 3) {
                TaskItem(
                    name       = parts[0],
                    pricePaise = parts[1].toLongOrNull() ?: 0L,
                    checked    = parts[2].toBooleanStrictOrNull() ?: false,
                )
            } else null
        }
    }

    @TypeConverter fun toTaskItemList(list: List<TaskItem>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString("|||") { "${it.name}:::${it.pricePaise}:::${it.checked}" }
    }
}
