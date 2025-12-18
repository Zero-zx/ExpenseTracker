package transaction.usecase

import transaction.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(
    private val repository: EventRepository
) {
    suspend operator fun invoke(eventId: Long) {
        val event = repository.getEventById(eventId)
        if (event != null) {
            repository.deleteEvent(event)
        }
    }
}

