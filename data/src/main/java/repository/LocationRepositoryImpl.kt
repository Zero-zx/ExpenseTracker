package repository

import dao.LocationDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import transaction.model.Location
import transaction.repository.LocationRepository
import javax.inject.Inject

internal class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
) : LocationRepository {

    override fun getAllLocationsByAccount(accountId: Long): Flow<List<Location>> {
        return locationDao.getAllLocationsByAccount(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchLocationsByAccount(accountId: Long, query: String): Flow<List<Location>> {
        val searchQuery = "%$query%"
        return locationDao.searchLocationsByAccount(accountId, searchQuery).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLocationById(locationId: Long): Location? {
        return locationDao.getLocationById(locationId)?.toDomain()
    }

    override suspend fun insertLocation(location: Location): Long {
        return locationDao.insertLocation(location.toEntity())
    }

    override suspend fun updateLocation(location: Location) {
        locationDao.updateLocation(location.toEntity())
    }

    override suspend fun deleteLocation(location: Location) {
        locationDao.deleteLocation(location.toEntity())
    }

    override suspend fun getLocationByName(name: String, accountId: Long): Location? {
        return locationDao.getLocationByName(name, accountId)?.toDomain()
    }
}
