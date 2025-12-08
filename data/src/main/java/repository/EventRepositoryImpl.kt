package repository

import dao.EventDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import transaction.model.Event
import transaction.model.EventParticipant
import transaction.model.EventWithParticipants
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

    override fun getEventWithParticipants(eventId: Long): Flow<EventWithParticipants?> {
        return eventDao.getEventWithParticipants(eventId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun insertEvent(event: Event): Long {
        return eventDao.insertEvent(event.toEntity())
    }

    override suspend fun insertParticipants(participants: List<EventParticipant>) {
        eventDao.insertParticipants(participants.map { it.toEntity() })
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event.toEntity())
    }

    override suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.toEntity())
    }

    override fun getParticipantsByEvent(eventId: Long): Flow<List<EventParticipant>> {
        return eventDao.getParticipantsByEvent(eventId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getEventById(eventId: Long): Event? {
        return eventDao.getEventById(eventId)?.toDomain()
    }
}

