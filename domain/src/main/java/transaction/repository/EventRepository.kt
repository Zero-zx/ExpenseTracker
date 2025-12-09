package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Event
import transaction.model.Payee
import transaction.model.EventWithPayees

interface EventRepository {
    fun getAllEventsByAccount(accountId: Long): Flow<List<Event>>

    fun getEventWithPayees(eventId: Long): Flow<EventWithPayees?>

    suspend fun insertEvent(event: Event): Long

    suspend fun insertParticipants(participants: List<Payee>)

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(event: Event)

    fun getPayeesByEvent(eventId: Long): Flow<List<Payee>>
    suspend fun getEventById(eventId: Long): Event?
}

