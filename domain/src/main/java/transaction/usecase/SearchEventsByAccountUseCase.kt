package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Event
import transaction.repository.EventRepository
import javax.inject.Inject

class SearchEventsByAccountUseCase @Inject constructor(
    private val repository: EventRepository
) {
    operator fun invoke(accountId: Long, searchQuery: String): Flow<List<Event>> {
        return repository.searchEventsByAccount(accountId, searchQuery)
    }
}