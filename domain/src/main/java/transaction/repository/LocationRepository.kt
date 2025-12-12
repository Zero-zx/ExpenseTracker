package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Location

interface LocationRepository {
    fun getAllLocationsByAccount(accountId: Long): Flow<List<Location>>
    fun searchLocationsByAccount(accountId: Long, query: String): Flow<List<Location>>
    suspend fun getLocationById(locationId: Long): Location?
    suspend fun insertLocation(location: Location): Long
    suspend fun getLocationByName(name: String, accountId: Long): Location?
}