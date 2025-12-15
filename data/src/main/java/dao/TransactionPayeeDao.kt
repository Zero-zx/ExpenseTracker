package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import model.TransactionPayeeEntity
import model.TransactionPayeePair

@Dao
internal interface TransactionPayeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionPayee(transactionPayee: TransactionPayeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionPayees(transactionPayees: List<TransactionPayeeEntity>)

    @Query("SELECT payeeId FROM tb_transaction_payee WHERE transactionId = :transactionId")
    suspend fun getPayeeIdsByTransaction(transactionId: Long): List<Long>

    @Query("SELECT transactionId, payeeId FROM tb_transaction_payee WHERE transactionId IN (:transactionIds)")
    suspend fun getPayeeIdsByTransactions(transactionIds: List<Long>): List<TransactionPayeePair>

    @Query("DELETE FROM tb_transaction_payee WHERE transactionId = :transactionId")
    suspend fun deletePayeesByTransaction(transactionId: Long)
}


