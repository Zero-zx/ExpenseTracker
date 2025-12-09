package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.EventWithPayees
import transaction.repository.EventRepository
import javax.inject.Inject

class GetEventWithPayeesUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(eventId: Long): Flow<EventWithPayees?> {
        return repository.getEventWithPayees(eventId)
    }
}

