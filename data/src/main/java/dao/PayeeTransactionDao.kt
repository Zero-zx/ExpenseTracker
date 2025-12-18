package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.PayeeEntity

@Dao
internal interface PayeeTransactionDao {
    @Query("SELECT * FROM tb_payee WHERE accountId = :accountId ORDER BY name ASC")
    fun getAllPayeesByAccount(accountId: Long): Flow<List<PayeeEntity>>

    @Query("SELECT * FROM tb_payee WHERE accountId = :accountId AND isFromContacts = 0 ORDER BY name ASC")
    fun getRecentPayeesByAccount(accountId: Long): Flow<List<PayeeEntity>>

    @Query("SELECT * FROM tb_payee WHERE id = :payeeId")
    suspend fun getPayeeById(payeeId: Long): PayeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayee(payee: PayeeEntity): Long

    @Update
    suspend fun updatePayee(payee: PayeeEntity)

    @Delete
    suspend fun deletePayee(payee: PayeeEntity)

    @Query("SELECT * FROM tb_payee WHERE name = :name AND accountId = :accountId LIMIT 1")
    suspend fun getPayeeByName(name: String, accountId: Long): PayeeEntity?
}


