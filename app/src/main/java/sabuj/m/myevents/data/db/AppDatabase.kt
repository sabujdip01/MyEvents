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
 * Version 6 — adds bookingMade column to bookings, totalAmountPaise and itemsEncoded to tasks.
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
    version = 6,
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

        val MIGRATION_3_4: Migration
            get() = object : Migration(3, 4) {
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

    }
}
