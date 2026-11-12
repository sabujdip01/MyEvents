package sabuj.m.myevents.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import sabuj.m.myevents.core.icons.EventIcon
import sabuj.m.myevents.data.db.dao.EventDao
import sabuj.m.myevents.data.db.entity.EventEntity
import sabuj.m.myevents.domain.model.Event
import sabuj.m.myevents.domain.model.SyncState
import sabuj.m.myevents.domain.repository.EventRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [EventRepository] backed by Room.
 * Maps between [EventEntity] (data layer) and [Event] (domain layer).
 */
@Singleton
class EventRepositoryImpl @Inject constructor(
    private val dao: EventDao,
    @param:dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
) : EventRepository {

    override fun observeAll(): Flow<List<Event>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Event?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun upsert(event: Event) {
        dao.upsert(event.toEntity())
        sabuj.m.myevents.data.worker.SyncWorker.enqueueImmediateSync(context)
    }

    override suspend fun softDelete(id: String) {
        dao.softDelete(id, System.currentTimeMillis())
        sabuj.m.myevents.data.worker.SyncWorker.enqueueImmediateSync(context)
    }

    override suspend fun restoreDeleted(id: String) {
        dao.restore(id, System.currentTimeMillis())
        sabuj.m.myevents.data.worker.SyncWorker.enqueueImmediateSync(context)
    }

    override suspend fun hardDelete(id: String) {
        dao.hardDelete(id)
        sabuj.m.myevents.data.worker.SyncWorker.enqueueImmediateSync(context)
    }

    override suspend fun isDateTaken(date: LocalDate, excludeId: String?): Boolean =
        dao.countByDate(date.toEpochDay(), excludeId) > 0

    // ── Mappers ─────────────────────────────────────────────────────────────

    private fun EventEntity.toDomain() = Event(
        id               = id,
        title            = title,
        eventName        = eventName,
        date             = LocalDate.ofEpochDay(date),
        icon             = runCatching { EventIcon.valueOf(iconKey) }.getOrDefault(EventIcon.Other),
        budgetPaise      = budgetPaise,
        inviteesRequired = inviteesRequired,
        createdAt        = createdAt,
        updatedAt        = updatedAt,
        syncState        = runCatching { SyncState.valueOf(syncState) }.getOrDefault(SyncState.PENDING),
        deleted          = deleted == 1,
    )

    private fun Event.toEntity() = EventEntity(
        id               = id,
        title            = title,
        eventName        = eventName,
        date             = date.toEpochDay(),
        iconKey          = icon.name,
        budgetPaise      = budgetPaise,
        inviteesRequired = inviteesRequired,
        createdAt        = createdAt,
        updatedAt        = updatedAt,
        syncState        = syncState.name,
        deleted          = if (deleted) 1 else 0,
    )
}

