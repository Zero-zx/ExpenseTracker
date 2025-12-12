package transaction.usecase

import transaction.model.Location
import transaction.repository.LocationRepository
import javax.inject.Inject

class AddLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(
        name: String,
        accountId: Long
    ): Long {
        require(name.isNotBlank()) { "Location name cannot be blank" }

        // Check if location already exists
        val existingLocation = repository.getLocationByName(name, accountId)
        if (existingLocation != null) {
            return existingLocation.id
        }

        val location = Location(
            name = name,
            accountId = accountId
        )

        return repository.insertLocation(location)
    }
}