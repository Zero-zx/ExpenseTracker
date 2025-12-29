package transaction.usecase

import transaction.repository.LocationRepository
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(locationId: Long) {
        val location = repository.getLocationById(locationId)
        if (location != null) {
            repository.deleteLocation(location)
        }
    }
}

