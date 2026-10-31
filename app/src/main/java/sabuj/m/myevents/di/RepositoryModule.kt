package sabuj.m.myevents.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sabuj.m.myevents.data.repository.BookingRepositoryImpl
import sabuj.m.myevents.data.repository.EventRepositoryImpl
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their implementations.
 * Add a @Binds entry here for each new repository as phases complete.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: BookingRepositoryImpl): BookingRepository
}
