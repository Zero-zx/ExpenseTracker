package repository

import dao.TransactionDao
import dao.TransactionPayeeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import model.TransactionPayeeEntity
import session.UserSessionManager
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

internal class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionPayeeDao: TransactionPayeeDao,
    private val sessionManager: UserSessionManager
) : TransactionRepository {
    override fun getAllTransactionByAccount(accountId: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionByAccountId(accountId).map { it ->
            it.map {
                it.toDomain()
            }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        val userId = sessionManager.getCurrentUserId()
        return transactionDao.insert(transaction.toEntity(userId))
    }

    override suspend fun getTransactionById(transactionId: Long): Transaction? {
        val transactionWithDetails = transactionDao.getTransactionById(transactionId) ?: return null
        // TransactionWithDetails already loads payees via @Relation, no need to load separately
        return transactionWithDetails.toDomain()
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val userId = sessionManager.getCurrentUserId()
        transactionDao.update(transaction.toEntity(userId))

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
        val userId = sessionManager.getCurrentUserId()
        transactionDao.delete(transaction.toEntity(userId))
    }

    override fun getTransactionsByDateRange(
        userId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(userId, startDate, endDate).map { it ->
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
        val userId = sessionManager.getCurrentUserId()
        return transactionDao.insertTransactionWithPayees(
            transaction.toEntity(userId),
            transaction.payees.map { it.toEntity(transaction.account.id) })
    }

    override fun getTotalBalance(): Flow<Double> {
        val userId = sessionManager.getCurrentUserId()
        return transactionDao.getTotalBalance(userId)
    }
}