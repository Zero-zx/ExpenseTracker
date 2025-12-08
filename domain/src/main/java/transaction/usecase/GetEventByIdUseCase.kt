package transaction.usecase

import transaction.model.Event
import transaction.repository.EventRepository
import javax.inject.Inject

class GetEventByIdUseCase @Inject constructor(
    private val eventRepository: EventRepository
){
    suspend operator fun invoke(eventId: Long): Event? {
        return eventRepository.getEventById(eventId)
    }
}