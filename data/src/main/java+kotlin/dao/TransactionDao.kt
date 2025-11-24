package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.TransactionEntity
import model.TransactionWithDetails

@Dao
internal interface TransactionDao {
    @Transaction
    @Query("SELECT * FROM tb_transaction WHERE account_id = :accountId")
    fun getAccountWithTransactions(accountId: Long): Flow<List<TransactionWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Transaction
    @Query("SELECT * FROM tb_transaction WHERE account_id = :accountId AND create_at BETWEEN :startDate AND :endDate")
    fun getTransactionsByDateRange(
        accountId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithDetails>>
}
