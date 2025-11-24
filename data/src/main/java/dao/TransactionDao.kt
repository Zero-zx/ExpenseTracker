package dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.TransactionWithDetails

@Dao
interface TransactionDao {
    @Query("SELECT * FROM tb_transaction WHERE account_id = :accountId")
    fun getAccountWithTransactions(accountId: Long): Flow<List<TransactionWithDetails>>
}