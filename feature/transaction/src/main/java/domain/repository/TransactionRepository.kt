package domain.repository

import kotlinx.coroutines.flow.Flow
import data.model.Transaction

interface TransactionRepository {
    fun getAllTransactionByAccount(accountId: Long): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    fun getTransactionsByDateRange(accountId: Long, startDate: Long, endDate: Long): Flow<List<Transaction>>
}