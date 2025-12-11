package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Transaction

interface TransactionRepository {
    fun getAllTransactionByAccount(accountId: Long): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun getTransactionById(transactionId: Long): Transaction?

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    fun getTransactionsByDateRange(accountId: Long, startDate: Long, endDate: Long): Flow<List<Transaction>>
}




