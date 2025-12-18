package transaction.usecase

import transaction.model.Event
import transaction.repository.EventRepository
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(event: Event) {
        require(event.eventName.isNotBlank()) {
            "Event name cannot be blank"
        }

        repository.updateEvent(event)
    }
}


