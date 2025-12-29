package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.PayeeEntity
import payee.model.PayeeType

@Dao
internal interface PayeeDao {
    @Query("SELECT * FROM tb_payee WHERE userId = :userId AND payeeType = :payeeType ORDER BY name ASC")
    fun getAllPayeesByUserId(userId: Long, payeeType: PayeeType): Flow<List<PayeeEntity>>

    @Query("SELECT * FROM tb_payee WHERE userId = :userId AND payeeType = :payeeType AND isFromContacts = 0 ORDER BY name ASC")
    fun getRecentPayeesByType(userId: Long, payeeType: PayeeType): Flow<List<PayeeEntity>>

    @Query("SELECT * FROM tb_payee WHERE id = :payeeId")
    suspend fun getPayeeById(payeeId: Long): PayeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayee(payee: PayeeEntity): Long

    @Update
    suspend fun updatePayee(payee: PayeeEntity)

    @Delete
    suspend fun deletePayee(payee: PayeeEntity)

    @Query("SELECT * FROM tb_payee WHERE name = :name AND payeeType = :payeeType AND userId = :accountId LIMIT 1")
    suspend fun getPayeeByNameAndType(name: String, payeeType: PayeeType, accountId: Long): PayeeEntity?

    @Query("SELECT * FROM tb_payee WHERE userId = :userId AND payeeType = :payeeType AND name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun searchPayeesByType(
        userId: Long,
        searchQuery: String,
        payeeType: PayeeType
    ): Flow<List<PayeeEntity>>
}