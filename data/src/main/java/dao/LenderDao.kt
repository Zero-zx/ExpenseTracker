package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.LenderEntity

@Dao
internal interface LenderDao {
    @Query("SELECT * FROM tb_lender WHERE accountId = :accountId ORDER BY name ASC")
    fun getAllLendersByAccount(accountId: Long): Flow<List<LenderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLender(lender: LenderEntity): Long

    @Update
    suspend fun updateLender(lender: LenderEntity)

    @Delete
    suspend fun deleteLender(lender: LenderEntity)

    @Query("SELECT * FROM tb_lender WHERE id = :lenderId")
    suspend fun getLenderById(lenderId: Long): LenderEntity?

    @Query("SELECT * FROM tb_lender WHERE name = :name AND accountId = :accountId LIMIT 1")
    suspend fun getLenderByName(name: String, accountId: Long): LenderEntity?

    @Query("SELECT * FROM tb_lender WHERE accountId = :accountId AND name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun searchLendersByAccount(accountId: Long, searchQuery: String): Flow<List<LenderEntity>>
}

