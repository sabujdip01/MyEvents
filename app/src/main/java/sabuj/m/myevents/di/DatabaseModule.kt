package sabuj.m.myevents.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sabuj.m.myevents.data.db.AppDatabase
import sabuj.m.myevents.data.db.dao.EventDao
import javax.inject.Singleton

/**
 * Hilt module that provides the [AppDatabase] and all DAOs as singletons.
 * New DAOs should be added here as more phases complete.
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
        ).build()

    @Provides
    fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()
}

