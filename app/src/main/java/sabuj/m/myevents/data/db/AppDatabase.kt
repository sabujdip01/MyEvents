package sabuj.m.myevents.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import sabuj.m.myevents.data.db.dao.BookingDao
import sabuj.m.myevents.data.db.dao.EventDao
import sabuj.m.myevents.data.db.dao.ExpenseDao
import sabuj.m.myevents.data.db.dao.GiftDao
import sabuj.m.myevents.data.db.dao.InviteeDao
import sabuj.m.myevents.data.db.dao.TaskDao
import sabuj.m.myevents.data.db.entity.BookingEntity
import sabuj.m.myevents.data.db.entity.BookingPaymentEntity
import sabuj.m.myevents.data.db.entity.EventEntity
import sabuj.m.myevents.data.db.entity.ExpenseEntity
import sabuj.m.myevents.data.db.entity.GiftEntity
import sabuj.m.myevents.data.db.entity.InviteeEntity
import sabuj.m.myevents.data.db.entity.TaskEntity

/**
 * Single Room database for the app.
 * Version 8 — adds invitationType column to invitees.
 */
@Database(
    entities = [
        EventEntity::class,
        BookingEntity::class,
        BookingPaymentEntity::class,
        ExpenseEntity::class,
        InviteeEntity::class,
        GiftEntity::class,
        TaskEntity::class,
    ],
    version = 8,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun bookingDao(): BookingDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun inviteeDao(): InviteeDao
    abstract fun giftDao(): GiftDao
    abstract fun taskDao(): TaskDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
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
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_bookings_eventId` ON `bookings` (`eventId`)")

                db.execSQL(
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
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_booking_payments_bookingId` ON `booking_payments` (`bookingId`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `expenses` (
                        `id` TEXT NOT NULL,
                        `eventId` TEXT NOT NULL,
                        `groupId` TEXT NOT NULL,
                        `purpose` TEXT NOT NULL,
                        `amountPaise` INTEGER NOT NULL,
                        `amountDuePaise` INTEGER NOT NULL,
                        `itemsJson` TEXT NOT NULL,
                        `allReceived` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `syncState` TEXT NOT NULL DEFAULT 'PENDING',
                        `deleted` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`eventId`) REFERENCES `events`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_eventId` ON `expenses` (`eventId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_groupId` ON `expenses` (`groupId`)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `invitees` (
                        `id` TEXT NOT NULL,
                        `eventId` TEXT NOT NULL,
                        `groupId` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `expectedCount` INTEGER NOT NULL,
                        `address` TEXT NOT NULL,
                        `invited` INTEGER NOT NULL DEFAULT 0,
                        `note` TEXT NOT NULL DEFAULT '',
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `syncState` TEXT NOT NULL DEFAULT 'PENDING',
                        `deleted` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`eventId`) REFERENCES `events`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_invitees_eventId` ON `invitees` (`eventId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_invitees_groupId` ON `invitees` (`groupId`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `gifts` (
                        `id` TEXT NOT NULL,
                        `eventId` TEXT NOT NULL,
                        `giverName` TEXT NOT NULL,
                        `amountPaise` INTEGER NOT NULL,
                        `description` TEXT NOT NULL,
                        `inviteeId` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `syncState` TEXT NOT NULL DEFAULT 'PENDING',
                        `deleted` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`eventId`) REFERENCES `events`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_gifts_eventId` ON `gifts` (`eventId`)")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tasks` (
                        `id` TEXT NOT NULL,
                        `eventId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `dueDateEpochDay` INTEGER,
                        `completed` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        `syncState` TEXT NOT NULL DEFAULT 'PENDING',
                        `deleted` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`eventId`) REFERENCES `events`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_eventId` ON `tasks` (`eventId`)")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `bookings` ADD COLUMN `bookingMade` INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `totalAmountPaise` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `itemsEncoded` TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `tasks` ADD COLUMN `icon` TEXT NOT NULL DEFAULT 'Cake'")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `invitees` ADD COLUMN `invitationType` TEXT NOT NULL DEFAULT 'None'")
            }
        }
    }
}
