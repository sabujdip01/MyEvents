package sabuj.m.myevents.domain.usecase

import sabuj.m.myevents.data.db.dao.BookingDao
import sabuj.m.myevents.data.db.dao.ExpenseDao
import sabuj.m.myevents.data.db.dao.GiftDao
import sabuj.m.myevents.data.db.dao.InviteeDao
import sabuj.m.myevents.data.db.dao.TaskDao
import sabuj.m.myevents.domain.repository.EventRepository
import javax.inject.Inject

/**
 * Soft-deletes an event and all associated bookings, expenses, invitees, tasks, and gifts.
 */
class SoftDeleteEventUseCase @Inject constructor(
    private val repo: EventRepository,
    private val bookingDao: BookingDao,
    private val expenseDao: ExpenseDao,
    private val inviteeDao: InviteeDao,
    private val taskDao: TaskDao,
    private val giftDao: GiftDao,
) {
    suspend operator fun invoke(id: String) {
        val now = System.currentTimeMillis()
        repo.softDelete(id)
        bookingDao.softDeleteByEvent(id, now)
        expenseDao.softDeleteByEvent(id, now)
        inviteeDao.softDeleteByEvent(id, now)
        taskDao.softDeleteByEvent(id, now)
        giftDao.softDeleteByEvent(id, now)
    }
}

/** Restores a soft-deleted event and all associated records (called on Undo). */
class RestoreEventUseCase @Inject constructor(
    private val repo: EventRepository,
    private val bookingDao: BookingDao,
    private val expenseDao: ExpenseDao,
    private val inviteeDao: InviteeDao,
    private val taskDao: TaskDao,
    private val giftDao: GiftDao,
) {
    suspend operator fun invoke(id: String) {
        val now = System.currentTimeMillis()
        repo.restoreDeleted(id)
        bookingDao.restoreByEvent(id, now)
        expenseDao.restoreByEvent(id, now)
        inviteeDao.restoreByEvent(id, now)
        taskDao.restoreByEvent(id, now)
        giftDao.restoreByEvent(id, now)
    }
}

/** Permanently removes an event and all associated records once the Undo window expires. */
class HardDeleteEventUseCase @Inject constructor(
    private val repo: EventRepository,
    private val bookingDao: BookingDao,
    private val expenseDao: ExpenseDao,
    private val inviteeDao: InviteeDao,
    private val taskDao: TaskDao,
    private val giftDao: GiftDao,
) {
    suspend operator fun invoke(id: String) {
        repo.hardDelete(id)
        bookingDao.hardDeleteByEvent(id)
        expenseDao.hardDeleteByEvent(id)
        inviteeDao.hardDeleteByEvent(id)
        taskDao.hardDeleteByEvent(id)
        giftDao.hardDeleteByEvent(id)
    }
}

