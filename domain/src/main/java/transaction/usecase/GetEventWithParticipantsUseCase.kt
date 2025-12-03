package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.EventWithParticipants
import transaction.repository.EventRepository
import javax.inject.Inject

class GetEventWithParticipantsUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(eventId: Long): Flow<EventWithParticipants?> {
        return repository.getEventWithParticipants(eventId)
    }
}

