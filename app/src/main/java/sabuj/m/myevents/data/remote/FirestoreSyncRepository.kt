package sabuj.m.myevents.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import sabuj.m.myevents.data.db.dao.BookingDao
import sabuj.m.myevents.data.db.dao.EventDao
import sabuj.m.myevents.data.db.dao.ExpenseDao
import sabuj.m.myevents.data.db.dao.InviteeDao
import sabuj.m.myevents.data.db.dao.TaskDao
import sabuj.m.myevents.data.db.entity.BookingEntity
import sabuj.m.myevents.data.db.entity.BookingPaymentEntity
import sabuj.m.myevents.data.db.entity.EventEntity
import sabuj.m.myevents.data.db.entity.ExpenseEntity
import sabuj.m.myevents.data.db.entity.InviteeEntity
import sabuj.m.myevents.data.db.entity.TaskEntity
import sabuj.m.myevents.data.security.PinStore
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.BookingRepository
import sabuj.m.myevents.domain.repository.EventRepository
import sabuj.m.myevents.domain.repository.ExpenseRepository
import sabuj.m.myevents.domain.repository.InviteeRepository
import sabuj.m.myevents.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles real-time two-way sync between Room DB and Firebase Firestore.
 * Subscribes to Firestore snapshot listeners to push remote cloud changes instantly into Room.
 */
@Singleton
class FirestoreSyncRepository @Inject constructor(
    private val authManager: FirebaseAuthManager,
    private val settingsDataStore: sabuj.m.myevents.data.settings.SettingsDataStore,
    private val pinStore: PinStore,
    private val eventRepo: EventRepository,
    private val bookingRepo: BookingRepository,
    private val expenseRepo: ExpenseRepository,
    private val inviteeRepo: InviteeRepository,
    private val taskRepo: TaskRepository,
    private val eventDao: EventDao,
    private val bookingDao: BookingDao,
    private val expenseDao: ExpenseDao,
    private val inviteeDao: InviteeDao,
    private val taskDao: TaskDao,
) {
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()
    private val activeListeners = mutableListOf<ListenerRegistration>()
    private val scope = CoroutineScope(Dispatchers.IO)

    /** Runs a full two-way sync (pull remote + push local) and starts real-timeListeners. */
    suspend fun performSync(): Result<Unit> {
        return try {
            val uid = authManager.ensureSignedIn()
            val timestamp = System.currentTimeMillis()

            // Heartbeat document
            db.collection("users").document(uid).set(
                mapOf(
                    "uid" to uid,
                    "lastSyncTimestamp" to timestamp,
                    "lastSyncFormatted" to java.util.Date(timestamp).toString(),
                ),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()

            // 1. Sync PIN with Firebase
            if (!pinStore.isPinSet) {
                syncRemotePin(uid)
            } else {
                pinStore.getHash()?.let { pushPinToRemote(uid, it) }
            }

            // 2. Pull remote changes from Firebase into Room DB
            pullRemoteData(uid)

            // 3. Push pending local changes from Room DB to Firebase
            syncEvents(uid)
            syncBookings(uid)
            syncExpenses(uid)
            syncInvitees(uid)
            syncTasks(uid)

            // 4. Start Real-time Snapshot Listeners
            startRealtimeSync(uid)

            settingsDataStore.saveLastSyncTimestamp(timestamp)
            android.util.Log.d("SyncWorker", "Firebase real-time sync active for uid: $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SyncWorker", "Firebase sync failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    // ── Real-time Snapshot Listeners ──────────────────────────────────────────

    fun startRealtimeSync(uid: String) {
        stopRealtimeSync()

        // 1. Events Listener
        val eventsReg = db.collection("users").document(uid).collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        parseAndUpsertEvent(doc.id, data)
                    }
                }
            }
        activeListeners.add(eventsReg)

        // 2. Bookings Listener
        val bookingsReg = db.collection("users").document(uid).collection("bookings")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        parseAndUpsertBooking(doc.id, data)
                    }
                }
            }
        activeListeners.add(bookingsReg)

        // 3. Expenses Listener
        val expensesReg = db.collection("users").document(uid).collection("expenses")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        parseAndUpsertExpense(doc.id, data)
                    }
                }
            }
        activeListeners.add(expensesReg)

        // 4. Invitees Listener
        val inviteesReg = db.collection("users").document(uid).collection("invitees")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        parseAndUpsertInvitee(doc.id, data)
                    }
                }
            }
        activeListeners.add(inviteesReg)

        // 5. Tasks Listener
        val tasksReg = db.collection("users").document(uid).collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                scope.launch {
                    for (doc in snapshot.documents) {
                        val data = doc.data ?: continue
                        parseAndUpsertTask(doc.id, data)
                    }
                }
            }
        activeListeners.add(tasksReg)
    }

    fun stopRealtimeSync() {
        activeListeners.forEach { it.remove() }
        activeListeners.clear()
    }

    // ── PIN Sync ─────────────────────────────────────────────────────────────

    suspend fun syncRemotePin(uid: String) {
        try {
            val pinDoc = db.collection("users").document(uid).collection("settings").document("pin").get().await()
            val remoteHash = pinDoc.getString("hash")
            if (!remoteHash.isNullOrBlank()) {
                pinStore.savePin(remoteHash)
            }
        } catch (e: Exception) {
            android.util.Log.w("SyncWorker", "Failed to fetch remote PIN: ${e.message}")
        }
    }

    suspend fun pushPinToRemote(uid: String, pinHash: String) {
        try {
            db.collection("users").document(uid).collection("settings").document("pin").set(
                mapOf("hash" to pinHash, "updatedAt" to System.currentTimeMillis())
            ).await()
        } catch (e: Exception) {
            android.util.Log.w("SyncWorker", "Failed to push PIN to remote: ${e.message}")
        }
    }

    // ── Pull Remote Data ─────────────────────────────────────────────────────

    private suspend fun pullRemoteData(uid: String) {
        pullEvents(uid)
        pullBookings(uid)
        pullExpenses(uid)
        pullInvitees(uid)
        pullTasks(uid)
    }

    private suspend fun pullEvents(uid: String) {
        try {
            val snap = db.collection("users").document(uid).collection("events").get().await()
            for (doc in snap.documents) {
                val data = doc.data ?: continue
                parseAndUpsertEvent(doc.id, data)
            }
        } catch (e: Exception) {
            android.util.Log.w("SyncWorker", "pullEvents failed: ${e.message}")
        }
    }

    private suspend fun pullBookings(uid: String) {
        try {
            val snap = db.collection("users").document(uid).collection("bookings").get().await()
            for (doc in snap.documents) {
                val data = doc.data ?: continue
                parseAndUpsertBooking(doc.id, data)
            }
        } catch (e: Exception) {
            android.util.Log.w("SyncWorker", "pullBookings failed: ${e.message}")
        }
    }

    private suspend fun pullExpenses(uid: String) {
        try {
            val snap = db.collection("users").document(uid).collection("expenses").get().await()
            for (doc in snap.documents) {
                val data = doc.data ?: continue
                parseAndUpsertExpense(doc.id, data)
            }
        } catch (e: Exception) {
            android.util.Log.w("SyncWorker", "pullExpenses failed: ${e.message}")
        }
    }

    private suspend fun pullInvitees(uid: String) {
        try {
            val snap = db.collection("users").document(uid).collection("invitees").get().await()
            for (doc in snap.documents) {
                val data = doc.data ?: continue
                parseAndUpsertInvitee(doc.id, data)
            }
        } catch (e: Exception) {
            android.util.Log.w("SyncWorker", "pullInvitees failed: ${e.message}")
        }
    }

    private suspend fun pullTasks(uid: String) {
        try {
            val snap = db.collection("users").document(uid).collection("tasks").get().await()
            for (doc in snap.documents) {
                val data = doc.data ?: continue
                parseAndUpsertTask(doc.id, data)
            }
        } catch (e: Exception) {
            android.util.Log.w("SyncWorker", "pullTasks failed: ${e.message}")
        }
    }

    // ── Parsing & Room Upserts ───────────────────────────────────────────────

    private suspend fun parseAndUpsertEvent(id: String, data: Map<String, Any?>) {
        val title = data["title"] as? String ?: return
        val eventName = (data["eventName"] as? String) ?: title
        val dateStr = data["date"] as? String
        val epochDay = dateStr?.let { runCatching { LocalDate.parse(it).toEpochDay() }.getOrNull() }
            ?: LocalDate.now().toEpochDay()
        val iconKey = (data["icon"] as? String) ?: sabuj.m.myevents.core.icons.EventIcon.Other.name
        val budgetPaise = (data["budgetPaise"] as? Number)?.toLong() ?: 0L
        val inviteesRequired = (data["inviteesRequired"] as? Boolean) ?: false
        val createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val deleted = if ((data["deleted"] as? Boolean) == true) 1 else 0

        val entity = EventEntity(
            id = id,
            title = title,
            eventName = eventName,
            date = epochDay,
            iconKey = iconKey,
            budgetPaise = budgetPaise,
            inviteesRequired = inviteesRequired,
            createdAt = createdAt,
            updatedAt = updatedAt,
            syncState = "SYNCED",
            deleted = deleted,
        )
        eventDao.upsert(entity)
    }

    private suspend fun parseAndUpsertBooking(id: String, data: Map<String, Any?>) {
        val eventId = data["eventId"] as? String ?: return
        val title = (data["title"] as? String) ?: ""
        val bookingType = (data["bookingType"] as? String) ?: "Booking"
        val bookingIconKey = (data["bookingIcon"] as? String) ?: bookingType
        val contactName = (data["contactName"] as? String) ?: ""
        val phone = (data["phone"] as? String) ?: ""
        val totalAmountPaise = (data["totalAmountPaise"] as? Number)?.toLong() ?: 0L
        val nextDueDateStr = data["nextDueDate"] as? String
        val nextDueDateEpochDay = nextDueDateStr?.let { runCatching { LocalDate.parse(it).toEpochDay() }.getOrNull() }
        val bookingMade = if ((data["bookingMade"] as? Boolean) == true) 1 else 0
        val createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val deleted = if ((data["deleted"] as? Boolean) == true) 1 else 0

        val bookingEntity = BookingEntity(
            id = id,
            eventId = eventId,
            title = title,
            bookingType = bookingType,
            bookingIconKey = bookingIconKey,
            contactName = contactName,
            phone = phone,
            totalAmountPaise = totalAmountPaise,
            nextDueDateEpochDay = nextDueDateEpochDay,
            bookingMade = bookingMade,
            createdAt = createdAt,
            updatedAt = updatedAt,
            syncState = "SYNCED",
            deleted = deleted,
        )

        @Suppress("UNCHECKED_CAST")
        val rawPayments = data["payments"] as? List<Map<String, Any?>> ?: emptyList()
        val paymentEntities = rawPayments.mapNotNull { pMap ->
            val pId = pMap["id"] as? String ?: return@mapNotNull null
            val pDateStr = pMap["date"] as? String
            val pDateEpochDay = pDateStr?.let { runCatching { LocalDate.parse(it).toEpochDay() }.getOrNull() }
                ?: LocalDate.now().toEpochDay()
            val pAmountPaise = (pMap["amountPaise"] as? Number)?.toLong() ?: 0L
            val pNote = (pMap["note"] as? String) ?: ""
            val pCreatedAt = (pMap["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            val pUpdatedAt = (pMap["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()

            BookingPaymentEntity(
                id = pId,
                bookingId = id,
                dateEpochDay = pDateEpochDay,
                amountPaise = pAmountPaise,
                note = pNote,
                createdAt = pCreatedAt,
                updatedAt = pUpdatedAt,
            )
        }

        bookingDao.updateBookingAndPayments(bookingEntity, paymentEntities)
    }

    private suspend fun parseAndUpsertExpense(id: String, data: Map<String, Any?>) {
        val eventId = data["eventId"] as? String ?: return
        val groupId = (data["groupId"] as? String) ?: id
        val purpose = (data["purpose"] as? String) ?: ""
        val amountPaise = (data["amountPaise"] as? Number)?.toLong() ?: 0L
        val amountDuePaise = (data["amountDuePaise"] as? Number)?.toLong() ?: 0L
        val rawItems = data["items"]
        val itemsJson = if (rawItems is String) rawItems else if (rawItems is List<*>) rawItems.joinToString(",") else ""
        val allReceived = if ((data["allReceived"] as? Boolean) == true) 1 else 0
        val createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val deleted = if ((data["deleted"] as? Boolean) == true) 1 else 0

        val entity = ExpenseEntity(
            id = id,
            eventId = eventId,
            groupId = groupId,
            purpose = purpose,
            amountPaise = amountPaise,
            amountDuePaise = amountDuePaise,
            itemsJson = itemsJson,
            allReceived = allReceived,
            createdAt = createdAt,
            updatedAt = updatedAt,
            syncState = "SYNCED",
            deleted = deleted,
        )
        expenseDao.upsertExpense(entity)
    }

    private suspend fun parseAndUpsertInvitee(id: String, data: Map<String, Any?>) {
        val eventId = data["eventId"] as? String ?: return
        val groupId = (data["groupId"] as? String) ?: id
        val name = (data["name"] as? String) ?: (data["relativeName"] as? String) ?: ""
        val expectedCount = (data["expectedCount"] as? Number)?.toInt() ?: 1
        val address = (data["address"] as? String) ?: ""
        val invited = if ((data["invited"] as? Boolean) == true) 1 else 0
        val note = (data["note"] as? String) ?: ""
        val invitationType = (data["invitationType"] as? String) ?: "None"
        val createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val deleted = if ((data["deleted"] as? Boolean) == true) 1 else 0

        val entity = InviteeEntity(
            id = id,
            eventId = eventId,
            groupId = groupId,
            name = name,
            expectedCount = expectedCount,
            address = address,
            invited = invited,
            note = note,
            createdAt = createdAt,
            updatedAt = updatedAt,
            syncState = "SYNCED",
            deleted = deleted,
            invitationType = invitationType,
        )
        inviteeDao.upsertInvitee(entity)
    }

    private suspend fun parseAndUpsertTask(id: String, data: Map<String, Any?>) {
        val eventId = data["eventId"] as? String ?: return
        val title = (data["title"] as? String) ?: ""
        val icon = (data["icon"] as? String) ?: "Cake"
        val dueDateStr = data["dueDate"] as? String
        val dueDateEpochDay = dueDateStr?.let { runCatching { LocalDate.parse(it).toEpochDay() }.getOrNull() }
        val totalAmountPaise = (data["totalAmountPaise"] as? Number)?.toLong() ?: 0L
        val rawItems = data["items"]
        val itemsEncoded = if (rawItems is String) rawItems else ""
        val completed = if ((data["completed"] as? Boolean) == true || (data["done"] as? Boolean) == true) 1 else 0
        val createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        val deleted = if ((data["deleted"] as? Boolean) == true) 1 else 0

        val entity = TaskEntity(
            id = id,
            eventId = eventId,
            title = title,
            icon = icon,
            dueDateEpochDay = dueDateEpochDay,
            totalAmountPaise = totalAmountPaise,
            itemsEncoded = itemsEncoded,
            completed = completed,
            createdAt = createdAt,
            updatedAt = updatedAt,
            syncState = "SYNCED",
            deleted = deleted,
        )
        taskDao.upsertTask(entity)
    }

    // ── Push Local Pending Data ──────────────────────────────────────────────

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

    private suspend fun syncBookings(uid: String) {
        val pending = bookingRepo.observeAll().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("bookings")

        pending.forEach { booking ->
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

    private suspend fun syncTasks(uid: String) {
        val pending = taskRepo.observeAllTasks().first()
            .filter { it.syncState == SyncState.PENDING }
        val col = db.collection("users").document(uid).collection("tasks")

        pending.forEach { task ->
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
