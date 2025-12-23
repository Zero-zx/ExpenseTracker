package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.BorrowerEntity

@Dao
internal interface BorrowerDao {

    @Query("SELECT * FROM tb_borrower WHERE accountId = :accountId ORDER BY name ASC")
    fun getAllBorrowersByAccount(accountId: Long): Flow<List<BorrowerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBorrower(borrower: BorrowerEntity): Long

    @Update
    suspend fun updateBorrower(borrower: BorrowerEntity)

    @Delete
    suspend fun deleteBorrower(borrower: BorrowerEntity)

    @Query("SELECT * FROM tb_borrower WHERE id = :borrowerId")
    suspend fun getBorrowerById(borrowerId: Long): BorrowerEntity?

    @Query("SELECT * FROM tb_borrower WHERE name = :name AND accountId = :accountId LIMIT 1")
    suspend fun getBorrowerByName(name: String, accountId: Long): BorrowerEntity?

    @Query("SELECT * FROM tb_borrower WHERE accountId = :accountId AND name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun searchBorrowersByAccount(accountId: Long, searchQuery: String): Flow<List<BorrowerEntity>>
}