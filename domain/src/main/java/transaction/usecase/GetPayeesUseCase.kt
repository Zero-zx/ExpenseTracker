package transaction.usecase

import transaction.repository.EventRepository
import javax.inject.Inject

class GetPayeesUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(eventId: Long) = repository.getPayeesByEvent(eventId)
}