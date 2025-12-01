package repository

import dao.TransactionDao
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactionByAccount(accountId: Long): Flow<List<Transaction>> {
        return transactionDao.getAccountWithTransactions(accountId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    override fun getTransactionsByDateRange(
        accountId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(accountId, startDate, endDate)
            .map { list ->
                list.map { it.toDomain() }
            }
    }
}