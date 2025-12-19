package repository

import dao.PayeeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import transaction.model.Payee
import transaction.repository.PayeeRepository
import javax.inject.Inject

internal class PayeeRepositoryImpl @Inject constructor(
    private val payeeDao: PayeeDao
) : PayeeRepository {

    override fun getAllPayeesByAccount(accountId: Long): Flow<List<Payee>> {
        return payeeDao.getAllPayeesByUserId(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentPayeesByAccount(accountId: Long): Flow<List<Payee>> {
        return payeeDao.getRecentPayeesByUserId(accountId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPayeeById(payeeId: Long): Payee? {
        return payeeDao.getPayeeById(payeeId)?.toDomain()
    }

    override suspend fun insertPayee(payee: Payee): Long {
        return payeeDao.insertPayee(payee.toEntity())
    }

    override suspend fun updatePayee(payee: Payee) {
        payeeDao.updatePayee(payee.toEntity())
    }

    override suspend fun deletePayee(payee: Payee) {
        payeeDao.deletePayee(payee.toEntity())
    }

    override suspend fun getPayeeByName(name: String, accountId: Long): Payee? {
        return payeeDao.getPayeeByName(name, accountId)?.toDomain()
    }
}