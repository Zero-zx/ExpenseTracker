package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Event

interface EventRepository {
    fun getAllEventsByAccount(accountId: Long): Flow<List<Event>>

    suspend fun insertEvent(event: Event): Long

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(event: Event)

    suspend fun getEventById(eventId: Long): Event?

    suspend fun getEventByName(eventName: String, accountId: Long): Event?

    fun searchEventsByAccount(accountId: Long, searchQuery: String): Flow<List<Event>>
}