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
        return transactionDao.getTransactionByAccountId(accountId).map { it ->
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insert(transaction.toEntity())
    }

    override suspend fun getTransactionById(transactionId: Long): Transaction? {
        val transactionWithDetails = transactionDao.getTransactionById(transactionId) ?: return null
        // TransactionWithDetails already loads payees via @Relation, no need to load separately
        return transactionWithDetails.toDomain()
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
        
        // Update payees - delete old ones and insert new ones
        transactionPayeeDao.deletePayeesByTransaction(transaction.id)
        val payeeIds = transaction.payees.map { it.id }
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
        return transactionDao.getTransactionsByDateRange(accountId, startDate, endDate).map { it ->
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getCategoryUsageCount(accountId: Long): Map<Long, Int> {
        return transactionDao.getCategoryUsageCount(accountId)
            .associate { it.categoryId to it.usageCount }
    }

    override suspend fun insertTransactionWithPayees(transaction: Transaction): Long {
        return transactionDao.insertTransactionWithPayees(transaction.toEntity(), transaction.payees.map { it.toEntity() })
    }
}