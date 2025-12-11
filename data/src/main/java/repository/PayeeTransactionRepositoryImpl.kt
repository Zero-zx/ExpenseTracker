package repository

import dao.PayeeTransactionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import transaction.model.PayeeTransaction
import transaction.repository.PayeeTransactionRepository
import javax.inject.Inject

internal class PayeeTransactionRepositoryImpl @Inject constructor(
    private val payeeTransactionDao: PayeeTransactionDao
) : PayeeTransactionRepository {

    override fun getAllPayeesByAccount(accountId: Long): Flow<List<PayeeTransaction>> {
        return payeeTransactionDao.getAllPayeesByAccount(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentPayeesByAccount(accountId: Long): Flow<List<PayeeTransaction>> {
        return payeeTransactionDao.getRecentPayeesByAccount(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPayeeById(payeeId: Long): PayeeTransaction? {
        return payeeTransactionDao.getPayeeById(payeeId)?.toDomain()
    }

    override suspend fun insertPayee(payee: PayeeTransaction): Long {
        return payeeTransactionDao.insertPayee(payee.toEntity())
    }

    override suspend fun getPayeeByName(name: String, accountId: Long): PayeeTransaction? {
        return payeeTransactionDao.getPayeeByName(name, accountId)?.toDomain()
    }
}


