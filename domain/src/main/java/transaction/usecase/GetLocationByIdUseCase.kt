package transaction.usecase

import transaction.model.Location
import transaction.repository.LocationRepository
import javax.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(locationId: Long): Location? {
        return repository.getLocationById(locationId)
    }
}


