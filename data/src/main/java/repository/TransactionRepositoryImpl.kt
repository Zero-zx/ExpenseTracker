package repository

import dao.TransactionDao
import dao.TransactionPayeeDao
import model.TransactionPayeeEntity
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionPayeeDao: TransactionPayeeDao
) : TransactionRepository {
    override fun getAllTransactionByAccount(accountId: Long): Flow<List<Transaction>> {
        return transactionDao.getAccountWithTransactions(accountId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        val transactionId = transactionDao.insert(transaction.toEntity())
        
        // Insert payees if any
        val payeeIds = transaction.payeeIds
        if (payeeIds.isNotEmpty()) {
            val transactionPayees = payeeIds.map { payeeId ->
                TransactionPayeeEntity(
                    transactionId = transactionId,
                    payeeId = payeeId
                )
            }
            transactionPayeeDao.insertTransactionPayees(transactionPayees)
        }
        
        return transactionId
    }

    override suspend fun getTransactionById(transactionId: Long): Transaction? {
        return transactionDao.getTransactionById(transactionId)?.toDomain()
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
        
        // Update payees - delete old ones and insert new ones
        transactionPayeeDao.deletePayeesByTransaction(transaction.id)
        val payeeIds = transaction.payeeIds
        if (payeeIds.isNotEmpty()) {
            val transactionPayees = payeeIds.map { payeeId ->
                TransactionPayeeEntity(
                    transactionId = transaction.id,
                    payeeId = payeeId
                )
            }
            transactionPayeeDao.insertTransactionPayees(transactionPayees)
        }
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