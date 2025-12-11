package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Location
import transaction.repository.LocationRepository
import javax.inject.Inject

class GetLocationsByAccountUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Location>> {
        return repository.getAllLocationsByAccount(accountId)
    }
}


