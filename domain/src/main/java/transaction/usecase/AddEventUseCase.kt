package transaction.usecase

import transaction.model.Event
import transaction.model.EventParticipant
import transaction.repository.EventRepository
import javax.inject.Inject

class AddEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(
        eventName: String,
        startDate: Long,
        endDate: Long?,
        numberOfParticipants: Int,
        accountId: Long,
        participants: List<String>
    ): Long {
        require(eventName.isNotBlank()) { "Event name cannot be blank" }
        require(numberOfParticipants > 0) { "Number of participants must be greater than 0" }
        require(participants.size == numberOfParticipants) {
            "Number of participants must match the actual list size"
        }

        if (endDate != null) {
            require(endDate >= startDate) { "End date must be after or equal to start date" }
        }

        val event = Event(
            eventName = eventName,
            startDate = startDate,
            endDate = endDate,
            numberOfParticipants = numberOfParticipants,
            accountId = accountId,
            isActive = true
        )

        val eventId = repository.insertEvent(event)

        val eventParticipants = participants.map { name ->
            EventParticipant(
                eventId = eventId,
                participantName = name
            )
        }

        repository.insertParticipants(eventParticipants)

        return eventId
    }
}

