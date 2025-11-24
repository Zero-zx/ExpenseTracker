package repository

import dao.TransactionDao
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import data.model.Transaction
import model.toTransaction
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun getAllTransactionByAccount(accountId: Long): Flow<List<Transaction>> {
        return transactionDao.getAccountWithTransactions(accountId)
            .map { list -> list.map { it.toTransaction() } }
    }
}