package dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.PayeeEntity
import model.TransactionEntity
import model.TransactionPayeeEntity
import model.TransactionWithDetails

@Dao
internal interface TransactionDao {
    // Get all transactions for an account
    @Transaction
    @Query("SELECT * FROM tb_transaction WHERE account_id = :accountId")
    fun getTransactionByAccountId(accountId: Long): Flow<List<TransactionWithDetails>>

    // insert new transaction to database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudentCourses(transactionPayees: List<TransactionPayeeEntity>)

    @Transaction
    suspend fun insertTransactionWithPayees(
        transaction: TransactionEntity,
        payees: List<PayeeEntity>
    ): Long {
        // 1. Insert student and get generated ID
        val transactionId = insert(transaction)

        // 2. Create junction table records
        if (payees.isNotEmpty()) {
            val transactionPayees = payees.map { payee ->
                TransactionPayeeEntity(
                    payeeId = payee.id,
                    transactionId = transactionId
                )
            }
            insertStudentCourses(transactionPayees)
        }

        return transactionId
    }

    // update an existed transaction
    @Update
    suspend fun update(transaction: TransactionEntity)

    // delete an existed transaction
    @Delete
    suspend fun delete(transaction: TransactionEntity)

    // get all transactions in a date range for an account
    @Transaction
    @Query("SELECT * FROM tb_transaction WHERE account_id = :accountId AND create_at BETWEEN :startDate AND :endDate")
    fun getTransactionsByDateRange(
        accountId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithDetails>>

    // get transaction by id
    @Transaction
    @Query("SELECT * FROM tb_transaction WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): TransactionWithDetails?

    // Get category usage count (number of times each category is used)
    @Query(
        """
        SELECT category_id, COUNT(*) as usage_count
        FROM tb_transaction
        WHERE account_id = :accountId
        GROUP BY category_id
        ORDER BY usage_count DESC
    """
    )
    suspend fun getCategoryUsageCount(accountId: Long): List<CategoryUsageCount>
}

data class CategoryUsageCount(
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    @ColumnInfo(name = "usage_count")
    val usageCount: Int
)
