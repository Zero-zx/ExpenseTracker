package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Location
import transaction.repository.LocationRepository
import javax.inject.Inject

class SearchLocationsByAccountUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(accountId: Long, query: String): Flow<List<Location>> {
        return repository.searchLocationsByAccount(accountId, query)
    }
}