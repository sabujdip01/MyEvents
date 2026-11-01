package sabuj.m.myevents.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import sabuj.m.myevents.domain.repository.ExpenseRepository
import sabuj.m.myevents.domain.repository.GiftRepository
import sabuj.m.myevents.domain.repository.InviteeRepository
import sabuj.m.myevents.domain.repository.TaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSyncRepository @Inject constructor(
    private val authManager: FirebaseAuthManager,
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val expenseRepo: ExpenseRepository,
    private val inviteeRepo: InviteeRepository,
    private val giftRepo: GiftRepository,
    private val taskRepo: TaskRepository,
) {
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    suspend fun performSync(): Boolean {
        val uid = authManager.ensureSignedIn() ?: return false

        return try {
            syncEvents(uid)
            syncBookings(uid)
            syncExpenses(uid)
            syncInvitees(uid)
            syncGifts(uid)
            syncTasks(uid)
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun syncEvents(uid: String) {
        val events = eventRepo.observeAll().first()
        val pending = events.filter { it.syncState == SyncState.PENDING }
        val collection = db.collection("users").document(uid).collection("events")

        pending.forEach { event ->
            val data = hashMapOf(
                "id" to event.id,
                "title" to event.title,
                "eventName" to event.eventName,
                "date" to event.date.toString(),
                "icon" to event.icon.name,
                "budgetPaise" to event.budgetPaise,
                "inviteesRequired" to event.inviteesRequired,
                "updatedAt" to event.updatedAt,
                "deleted" to event.deleted,
            )
            collection.document(event.id).set(data).await()
            eventRepo.upsert(event.copy(syncState = SyncState.SYNCED))
        }
    }

    private suspend fun syncBookings(uid: String) {
        val bookings = bookingRepo.observeAll().first()
        val pending = bookings.filter { it.syncState == SyncState.PENDING }
        val collection = db.collection("users").document(uid).collection("bookings")

        pending.forEach { booking ->
            val data = hashMapOf(
                "id" to booking.id,
                "eventId" to booking.eventId,
                "title" to booking.title,
                "bookingType" to booking.bookingType,
                "contactName" to booking.contactName,
                "phone" to booking.phone,
                "totalAmountPaise" to booking.totalAmountPaise,
                "updatedAt" to booking.updatedAt,
                "deleted" to booking.deleted,
            )
            collection.document(booking.id).set(data).await()
            bookingRepo.upsertBooking(booking.copy(syncState = SyncState.SYNCED))
        }
    }

    private suspend fun syncExpenses(uid: String) {
        val expenses = expenseRepo.observeAllExpenses().first()
        val pending = expenses.filter { it.syncState == SyncState.PENDING }
        val collection = db.collection("users").document(uid).collection("expenses")

        pending.forEach { exp ->
            val data = hashMapOf(
                "id" to exp.id,
                "eventId" to exp.eventId,
                "groupId" to exp.groupId,
                "purpose" to exp.purpose,
                "amountPaise" to exp.amountPaise,
                "amountDuePaise" to exp.amountDuePaise,
                "items" to exp.items,
                "allReceived" to exp.allReceived,
                "updatedAt" to exp.updatedAt,
                "deleted" to exp.deleted,
            )
            collection.document(exp.id).set(data).await()
            expenseRepo.upsertExpense(exp.copy(syncState = SyncState.SYNCED))
        }
    }

    private suspend fun syncInvitees(uid: String) {
        val invitees = inviteeRepo.observeAllInvitees().first()
        val pending = invitees.filter { it.syncState == SyncState.PENDING }
        val collection = db.collection("users").document(uid).collection("invitees")

        pending.forEach { inv ->
            val data = hashMapOf(
                "id" to inv.id,
                "eventId" to inv.eventId,
                "groupId" to inv.groupId,
                "name" to inv.name,
                "expectedCount" to inv.expectedCount,
                "address" to inv.address,
                "invited" to inv.invited,
                "note" to inv.note,
                "updatedAt" to inv.updatedAt,
                "deleted" to inv.deleted,
            )
            collection.document(inv.id).set(data).await()
            inviteeRepo.upsertInvitee(inv.copy(syncState = SyncState.SYNCED))
        }
    }

    private suspend fun syncGifts(uid: String) {
        val gifts = giftRepo.observeAllGifts().first()
        val pending = gifts.filter { it.syncState == SyncState.PENDING }
        val collection = db.collection("users").document(uid).collection("gifts")

        pending.forEach { gift ->
            val data = hashMapOf(
                "id" to gift.id,
                "eventId" to gift.eventId,
                "giverName" to gift.giverName,
                "amountPaise" to gift.amountPaise,
                "description" to gift.description,
                "inviteeId" to gift.inviteeId,
                "updatedAt" to gift.updatedAt,
                "deleted" to gift.deleted,
            )
            collection.document(gift.id).set(data).await()
            giftRepo.upsertGift(gift.copy(syncState = SyncState.SYNCED))
        }
    }

    private suspend fun syncTasks(uid: String) {
        val tasks = taskRepo.observeAllTasks().first()
        val pending = tasks.filter { it.syncState == SyncState.PENDING }
        val collection = db.collection("users").document(uid).collection("tasks")

        pending.forEach { task ->
            val data = hashMapOf(
                "id" to task.id,
                "eventId" to task.eventId,
                "title" to task.title,
                "dueDate" to task.dueDate?.toString(),
                "completed" to task.completed,
                "updatedAt" to task.updatedAt,
                "deleted" to task.deleted,
            )
            collection.document(task.id).set(data).await()
            taskRepo.upsertTask(task.copy(syncState = SyncState.SYNCED))
        }
    }
}
