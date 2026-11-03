package sabuj.m.myevents.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sabuj.m.myevents.data.db.AppDatabase
import sabuj.m.myevents.data.db.dao.BookingDao
import sabuj.m.myevents.data.db.dao.EventDao
import sabuj.m.myevents.data.db.dao.ExpenseDao
import sabuj.m.myevents.data.db.dao.GiftDao
import sabuj.m.myevents.data.db.dao.InviteeDao
import sabuj.m.myevents.data.db.dao.TaskDao
import javax.inject.Singleton

/**
 * Hilt module that provides the [AppDatabase] and all DAOs as singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "myevents.db",
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6,
            )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()

    @Provides
    fun provideBookingDao(db: AppDatabase): BookingDao = db.bookingDao()

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideInviteeDao(db: AppDatabase): InviteeDao = db.inviteeDao()

    @Provides
    fun provideGiftDao(db: AppDatabase): GiftDao = db.giftDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}
