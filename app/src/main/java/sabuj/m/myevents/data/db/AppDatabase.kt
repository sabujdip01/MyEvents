package sabuj.m.myevents.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import sabuj.m.myevents.data.db.dao.BookingDao
import sabuj.m.myevents.data.db.dao.EventDao
import sabuj.m.myevents.data.db.entity.BookingEntity
import sabuj.m.myevents.data.db.entity.BookingPaymentEntity
import sabuj.m.myevents.data.db.entity.EventEntity

/**
 * Single Room database for the app.
 * Version 2 — adds bookings and booking_payments tables.
 * Schema exported to app/schemas/ (ksp arg in build.gradle.kts).
 *
 * Add new entities here as future phases complete; bump version and write a Migration.
 */
@Database(
    entities = [
        EventEntity::class,
        BookingEntity::class,
        BookingPaymentEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun bookingDao(): BookingDao

    companion object {
        /**
         * Migration from version 1 to 2.
         * Creates the [bookings] and [booking_payments] tables exactly as
         * Room would generate them from [BookingEntity] and [BookingPaymentEntity].
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create bookings table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bookings` (
                        `id` TEXT NOT NULL,
                        `eventId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `bookingType` TEXT NOT NULL,
                        `bookingIconKey` TEXT NOT NULL,
                        `contactName` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `totalAmountPaise` INTEGER NOT NULL,
                        `nextDueDateEpochDay` INTEGER,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `syncState` TEXT NOT NULL DEFAULT 'PENDING',
                        `deleted` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`eventId`) REFERENCES `events`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_bookings_eventId` ON `bookings` (`eventId`)"
                )

                // Create booking_payments table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `booking_payments` (
                        `id` TEXT NOT NULL,
                        `bookingId` TEXT NOT NULL,
                        `dateEpochDay` INTEGER NOT NULL,
                        `amountPaise` INTEGER NOT NULL,
                        `note` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`bookingId`) REFERENCES `bookings`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_booking_payments_bookingId` ON `booking_payments` (`bookingId`)"
                )
            }
        }
    }
}
