package sabuj.m.myevents.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sabuj.m.myevents.data.repository.BookingRepositoryImpl
import sabuj.m.myevents.data.repository.EventRepositoryImpl
import sabuj.m.myevents.data.repository.ExpenseRepositoryImpl
import sabuj.m.myevents.data.repository.GiftRepositoryImpl
import sabuj.m.myevents.data.repository.InviteeRepositoryImpl
import sabuj.m.myevents.data.repository.TaskRepositoryImpl
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import sabuj.m.myevents.domain.repository.ExpenseRepository
import sabuj.m.myevents.domain.repository.GiftRepository
import sabuj.m.myevents.domain.repository.InviteeRepository
import sabuj.m.myevents.domain.repository.TaskRepository
import javax.inject.Inject
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

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindInviteeRepository(impl: InviteeRepositoryImpl): InviteeRepository

    @Binds
    @Singleton
    abstract fun bindGiftRepository(impl: GiftRepositoryImpl): GiftRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}
