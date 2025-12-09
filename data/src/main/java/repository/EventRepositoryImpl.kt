package repository

import dao.EventDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import transaction.model.Event
import transaction.model.Payee
import transaction.model.EventWithPayees
import transaction.repository.EventRepository
import javax.inject.Inject

internal class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {

    override fun getAllEventsByAccount(accountId: Long): Flow<List<Event>> {
        return eventDao.getAllEventsByAccount(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventWithPayees(eventId: Long): Flow<EventWithPayees?> {
        return eventDao.getEventWithPayees(eventId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun insertEvent(event: Event): Long {
        return eventDao.insertEvent(event.toEntity())
    }

    override suspend fun insertParticipants(participants: List<Payee>) {
        eventDao.insertParticipants(participants.map { it.toEntity() })
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event.toEntity())
    }

    override suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.toEntity())
    }

    override fun getPayeesByEvent(eventId: Long): Flow<List<Payee>> {
        return eventDao.getPayeesByEvent(eventId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEventById(eventId: Long): Event? {
        return eventDao.getEventById(eventId)?.toDomain()
    }
}

