package transaction.usecase

import transaction.model.Location
import transaction.repository.LocationRepository
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(location: Location) {
        require(location.name.isNotBlank()) {
            "Location name cannot be blank"
        }

        repository.updateLocation(location)
    }
}

