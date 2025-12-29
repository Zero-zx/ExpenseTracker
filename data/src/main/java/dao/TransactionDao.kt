package dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import category.model.CategoryType
import kotlinx.coroutines.flow.Flow
import model.PayeeEntity
import model.TransactionEntity
import model.TransactionPayeeEntity
import model.TransactionWithDetails

@Dao
internal interface TransactionDao {
    // Get all transactions for an account
    @Transaction
    @Query("SELECT * FROM tb_transaction WHERE accountId = :accountId")
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
    @Query("SELECT * FROM tb_transaction WHERE userId = :userId AND create_at BETWEEN :startDate AND :endDate ORDER BY create_at ASC")
    fun getTransactionsByDateRange(
        userId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionWithDetails>>

    // get all transaction in a date range with a type
    @Transaction
    @Query(
        """
    SELECT t.* FROM tb_transaction t
    INNER JOIN tb_category c ON t.categoryId = c.id
    WHERE t.userId = :userId 
    AND t.create_at BETWEEN :startDate AND :endDate 
    AND c.type IN (:types)
"""
    )
    fun getTransactionsByTypeDateRange(
        userId: Long,
        startDate: Long,
        endDate: Long,
        types: List<CategoryType>
    ): Flow<List<TransactionWithDetails>>

    // get transaction by id
    @Transaction
    @Query("SELECT * FROM tb_transaction WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): TransactionWithDetails?

    // Get category usage count (number of times each category is used)
    @Query(
        """
        SELECT categoryId, COUNT(*) as usageCount
        FROM tb_transaction
        WHERE accountId = :accountId
        GROUP BY categoryId
        ORDER BY usageCount DESC
    """
    )
    suspend fun getCategoryUsageCount(accountId: Long): List<CategoryUsageCount>

    @Query(
        """
        SELECT COALESCE(SUM(balance), 0)
        FROM tb_account 
        WHERE userId = :userId
    """
    )
    fun getTotalBalance(userId: Long): Flow<Double>
}

data class CategoryUsageCount(
    @ColumnInfo(name = "categoryId")
    val categoryId: Long,
    @ColumnInfo(name = "usageCount")
    val usageCount: Int
)
