package transaction.usecase

import session.repository.SessionRepository
import transaction.model.Event
import transaction.repository.EventRepository
import javax.inject.Inject

class AddEventUseCase @Inject constructor(
    private val repository: EventRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        event: Event
    ): Event {
        // Validate event name
        require(event.eventName.isNotBlank()) {
            "Event name cannot be blank"
        }

        // Validate number of participants matches
        require(event.participants?.size == event.numberOfParticipants) {
            "Number of participants must match the actual list size"
        }

        // Validate no duplicate participants
        require(event.participants?.size == event.participants?.distinct()?.size) {
            "Participant names must be unique"
        }

        // Validate no blank participant names
        require(event.participants?.all { it.isNotBlank() } == true) {
            "Participant names cannot be blank"
        }

        // Validate dates
        if (event.endDate != null) {
            require(event.endDate >= event.startDate) {
                "End date must be after or equal to start date"
            }
        }

        // Sanitize and create event
        val sanitizedParticipants = event.participants.map { it.trim() }
        val trimmedEventName = event.eventName.trim()

        // Check if event with same name already exists for this account
        val userId = sessionRepository.getCurrentUserId()
        val existingEvent = repository.getEventByName(trimmedEventName, userId)
        if (existingEvent != null) {
            // Event already exists, return it
            return existingEvent
        }

        val event = event.copy(
            eventName = trimmedEventName,
            participants = sanitizedParticipants
        )


        val eventId = repository.insertEvent(event)
        // Return event with the generated ID
        return event.copy(id = eventId)
    }

    suspend fun addEvent(
        eventName: String,
        startDate: Long,
        endDate: Long?,
        numberOfParticipants: Int? = 0,
        accountId: Long,
        participants: List<String>? = emptyList()
    ): Long {
        val event = Event(
            eventName = eventName,
            startDate = startDate,
            endDate = endDate,
            numberOfParticipants = numberOfParticipants,
            accountId = accountId,
            isActive = true,
            participants = participants,
        )

        return invoke(event).id
    }

}

