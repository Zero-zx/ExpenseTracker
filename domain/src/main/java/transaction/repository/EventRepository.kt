package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Event
import transaction.model.EventParticipant
import transaction.model.EventWithParticipants

interface EventRepository {
    fun getAllEventsByAccount(accountId: Long): Flow<List<Event>>

    fun getEventWithParticipants(eventId: Long): Flow<EventWithParticipants?>

    suspend fun insertEvent(event: Event): Long

    suspend fun insertParticipants(participants: List<EventParticipant>)

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(event: Event)

    fun getParticipantsByEvent(eventId: Long): Flow<List<EventParticipant>>
}

