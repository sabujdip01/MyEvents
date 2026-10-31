package sabuj.m.myevents.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sabuj.m.myevents.data.db.dao.EventDao
import sabuj.m.myevents.data.db.entity.EventEntity

/**
 * Single Room database for the app.
 * Version 1 — schema exported to app/schemas/ (ksp arg in build.gradle.kts).
 * Add new entities here as future phases complete; bump version and write a Migration.
 */
@Database(
    entities = [EventEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}

