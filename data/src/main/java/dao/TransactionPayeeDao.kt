package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import model.TransactionPayeeEntity

@Dao
internal interface TransactionPayeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionPayee(transactionPayee: TransactionPayeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionPayees(transactionPayees: List<TransactionPayeeEntity>)

    @Query("SELECT payeeId FROM tb_transaction_payee WHERE transactionId = :transactionId")
    suspend fun getPayeeIdsByTransaction(transactionId: Long): List<Long>

    @Query("DELETE FROM tb_transaction_payee WHERE transactionId = :transactionId")
    suspend fun deletePayeesByTransaction(transactionId: Long)
}

