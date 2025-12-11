package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.PayeeTransactionEntity

@Dao
internal interface PayeeTransactionDao {
    @Query("SELECT * FROM tb_payee_transaction WHERE accountId = :accountId ORDER BY name ASC")
    fun getAllPayeesByAccount(accountId: Long): Flow<List<PayeeTransactionEntity>>

    @Query("SELECT * FROM tb_payee_transaction WHERE accountId = :accountId AND isFromContacts = 0 ORDER BY name ASC")
    fun getRecentPayeesByAccount(accountId: Long): Flow<List<PayeeTransactionEntity>>

    @Query("SELECT * FROM tb_payee_transaction WHERE id = :payeeId")
    suspend fun getPayeeById(payeeId: Long): PayeeTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayee(payee: PayeeTransactionEntity): Long

    @Query("SELECT * FROM tb_payee_transaction WHERE name = :name AND accountId = :accountId LIMIT 1")
    suspend fun getPayeeByName(name: String, accountId: Long): PayeeTransactionEntity?
}


