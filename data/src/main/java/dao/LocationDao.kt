package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.LocationEntity

@Dao
internal interface LocationDao {
    @Query("SELECT * FROM tb_location WHERE accountId = :accountId ORDER BY name ASC")
    fun getAllLocationsByAccount(accountId: Long): Flow<List<LocationEntity>>

    @Query("SELECT * FROM tb_location WHERE accountId = :accountId AND name LIKE :query ORDER BY name ASC")
    fun searchLocationsByAccount(accountId: Long, query: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM tb_location WHERE id = :locationId")
    suspend fun getLocationById(locationId: Long): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long

    @Query("SELECT * FROM tb_location WHERE name = :name AND accountId = :accountId LIMIT 1")
    suspend fun getLocationByName(name: String, accountId: Long): LocationEntity?
}

