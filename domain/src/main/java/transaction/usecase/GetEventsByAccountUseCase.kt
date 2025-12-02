package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Event
import transaction.repository.EventRepository
import javax.inject.Inject

class GetEventsByAccountUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Event>> {
        return repository.getAllEventsByAccount(accountId)
    }
}

