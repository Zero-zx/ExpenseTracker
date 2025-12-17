package transaction.usecase

import transaction.model.Event
import transaction.repository.EventRepository
import javax.inject.Inject

class AddEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(
        eventName: String,
        startDate: Long,
        endDate: Long?,
        numberOfParticipants: Int? = 0,
        accountId: Long,
        participants: List<String>? = emptyList()
    ): Long {
        // Validate event name
        require(eventName.isNotBlank()) {
            "Event name cannot be blank"
        }

        // Validate number of participants matches
        require(participants?.size == numberOfParticipants) {
            "Number of participants must match the actual list size"
        }

        // Validate no duplicate participants
        require(participants?.size == participants?.distinct()?.size) {
            "Participant names must be unique"
        }

        // Validate no blank participant names
        require(participants?.all { it.isNotBlank() } == true) {
            "Participant names cannot be blank"
        }

        // Validate dates
        if (endDate != null) {
            require(endDate >= startDate) {
                "End date must be after or equal to start date"
            }
        }

        // Sanitize and create event
        val sanitizedParticipants = participants.map { it.trim() }
        val trimmedEventName = eventName.trim()

        // Check if event with same name already exists for this account
        val existingEvent = repository.getEventByName(trimmedEventName, accountId)
        if (existingEvent != null) {
            // Event already exists, return its ID
            return existingEvent.id
        }

        val event = Event(
            eventName = trimmedEventName,
            startDate = startDate,
            endDate = endDate,
            numberOfParticipants = numberOfParticipants,
            accountId = accountId,
            isActive = true,
            participants = sanitizedParticipants
        )

        return repository.insertEvent(event)
    }
}

