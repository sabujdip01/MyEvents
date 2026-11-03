package sabuj.m.myevents.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides [WorkManager].
 * Binds the singleton [WorkManager] instance for injection into repositories
 * and ViewModels that schedule background work.
 */
@Module
@InstallIn(SingletonComponent::class)
object WorkModule {

    /** Provides the [WorkManager] singleton scoped to the application. */
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)
}

