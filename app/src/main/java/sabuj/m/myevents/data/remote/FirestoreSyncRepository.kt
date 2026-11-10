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

/**
 * Pushes all PENDING local records to Firestore.
 * Uses anonymous auth (FirebaseAuthManager) to get the user's uid.
 * Each sync function:
 *   1. Fetches pending records from the local Room DB via the repository.
 *   2. Writes them to Firestore under users/{uid}/{collection}/{id}.
 *   3. Marks them SYNCED in Room so they won't be re-uploaded.
 */
@Singleton
class FirestoreSyncRepository @Inject constructor(
    private val authManager: FirebaseAuthManager,
    private val settingsDataStore: sabuj.m.myevents.data.settings.SettingsDataStore,
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val expenseRepo: ExpenseRepository,
    private val inviteeRepo: InviteeRepository,
    private val giftRepo: GiftRepository,
    private val taskRepo: TaskRepository,
) {
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /** Runs a full push-sync of all pending data. Returns true on success. */
    suspend fun performSync(): Boolean {
        val uid = authManager.ensureSignedIn() ?: return false

        return try {
            syncEvents(uid)
            syncBookings(uid)
            syncExpenses(uid)
            syncInvitees(uid)
            syncGifts(uid)
            syncTasks(uid)
            settingsDataStore.saveLastSyncTimestamp(System.currentTimeMillis())
            true
        } catch (e: Exception) {
            false
        }
    }

    // ── Events ───────────────────────────────────────────────────────────────

    private suspend fun syncEvents(uid: String) {
        val pending = eventRepo.observeAll().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("events")

        pending.forEach { event ->
            val data = hashMapOf(
                "id"               to event.id,
                "title"            to event.title,
                "eventName"        to event.eventName,
                "date"             to event.date.toString(),
                "icon"             to event.icon.name,
                "budgetPaise"      to event.budgetPaise,
                "inviteesRequired" to event.inviteesRequired,
                "createdAt"        to event.createdAt,
                "updatedAt"        to event.updatedAt,
                "deleted"          to event.deleted,
            )
            col.document(event.id).set(data).await()
            eventRepo.upsert(event.copy(syncState = SyncState.SYNCED))
        }
    }

    // ── Bookings ─────────────────────────────────────────────────────────────

    private suspend fun syncBookings(uid: String) {
        val pending = bookingRepo.observeAll().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("bookings")

        pending.forEach { booking ->
            // Serialise payments as a list of maps
            val paymentsData = booking.payments.map { p ->
                mapOf(
                    "id"          to p.id,
                    "bookingId"   to p.bookingId,
                    "date"        to p.date.toString(),
                    "amountPaise" to p.amountPaise,
                    "note"        to p.note,
                    "createdAt"   to p.createdAt,
                    "updatedAt"   to p.updatedAt,
                )
            }
            val data = hashMapOf(
                "id"               to booking.id,
                "eventId"          to booking.eventId,
                "title"            to booking.title,
                "bookingType"      to booking.bookingType,
                "bookingIcon"      to booking.bookingIcon.name,
                "contactName"      to booking.contactName,
                "phone"            to booking.phone,
                "totalAmountPaise" to booking.totalAmountPaise,
                "nextDueDate"      to booking.nextDueDate?.toString(),
                "bookingMade"      to booking.bookingMade,
                "payments"         to paymentsData,
                "createdAt"        to booking.createdAt,
                "updatedAt"        to booking.updatedAt,
                "deleted"          to booking.deleted,
            )
            col.document(booking.id).set(data).await()
            bookingRepo.upsertBooking(booking.copy(syncState = SyncState.SYNCED))
        }
    }

    // ── Expenses ─────────────────────────────────────────────────────────────

    private suspend fun syncExpenses(uid: String) {
        val pending = expenseRepo.observeAllExpenses().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("expenses")

        pending.forEach { exp ->
            val data = hashMapOf(
                "id"             to exp.id,
                "eventId"        to exp.eventId,
                "groupId"        to exp.groupId,
                "purpose"        to exp.purpose,
                "amountPaise"    to exp.amountPaise,
                "amountDuePaise" to exp.amountDuePaise,
                "items"          to exp.items,
                "allReceived"    to exp.allReceived,
                "createdAt"      to exp.createdAt,
                "updatedAt"      to exp.updatedAt,
                "deleted"        to exp.deleted,
            )
            col.document(exp.id).set(data).await()
            expenseRepo.upsertExpense(exp.copy(syncState = SyncState.SYNCED))
        }
    }

    // ── Invitees ─────────────────────────────────────────────────────────────

    private suspend fun syncInvitees(uid: String) {
        val pending = inviteeRepo.observeAllInvitees().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("invitees")

        pending.forEach { inv ->
            val data = hashMapOf(
                "id"            to inv.id,
                "eventId"       to inv.eventId,
                "groupId"       to inv.groupId,
                "name"          to inv.name,
                "expectedCount" to inv.expectedCount,
                "address"       to inv.address,
                "invited"       to inv.invited,
                "note"          to inv.note,
                "createdAt"      to inv.createdAt,
                "updatedAt"      to inv.updatedAt,
                "deleted"        to inv.deleted,
                "invitationType" to inv.invitationType.name,
            )
            col.document(inv.id).set(data).await()
            inviteeRepo.upsertInvitee(inv.copy(syncState = SyncState.SYNCED))
        }
    }

    // ── Gifts ────────────────────────────────────────────────────────────────

    private suspend fun syncGifts(uid: String) {
        val pending = giftRepo.observeAllGifts().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("gifts")

        pending.forEach { gift ->
            val data = hashMapOf(
                "id"          to gift.id,
                "eventId"     to gift.eventId,
                "giverName"   to gift.giverName,
                "amountPaise" to gift.amountPaise,
                "description" to gift.description,
                "inviteeId"   to gift.inviteeId,
                "createdAt"   to gift.createdAt,
                "updatedAt"   to gift.updatedAt,
                "deleted"     to gift.deleted,
            )
            col.document(gift.id).set(data).await()
            giftRepo.upsertGift(gift.copy(syncState = SyncState.SYNCED))
        }
    }

    // ── Tasks ────────────────────────────────────────────────────────────────

    private suspend fun syncTasks(uid: String) {
        val pending = taskRepo.observeAllTasks().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("tasks")

        pending.forEach { task ->
            // Serialise TaskItems as a list of maps
            val itemsData = task.items.map { item ->
                mapOf(
                    "name"       to item.name,
                    "pricePaise" to item.pricePaise,
                    "checked"    to item.checked,
                )
            }
            val data = hashMapOf(
                "id"               to task.id,
                "eventId"          to task.eventId,
                "title"            to task.title,
                "dueDate"          to task.dueDate?.toString(),
                "totalAmountPaise" to task.totalAmountPaise,
                "items"            to itemsData,
                "completed"        to task.completed,
                "createdAt"        to task.createdAt,
                "updatedAt"        to task.updatedAt,
                "deleted"          to task.deleted,
            )
            col.document(task.id).set(data).await()
            taskRepo.upsertTask(task.copy(syncState = SyncState.SYNCED))
        }
    }
}
