package repository

import dao.PayeeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import payee.model.Payee
import payee.model.PayeeType
import transaction.repository.PayeeRepository
import javax.inject.Inject

internal class PayeeRepositoryImpl @Inject constructor(
    private val payeeDao: PayeeDao
) : PayeeRepository {

    override fun getAllPayeesByType(userId: Long, payeeType: PayeeType): Flow<List<Payee>> {
        return payeeDao.getAllPayeesByUserId(userId, payeeType).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentPayeesByType(userId: Long, payeeType: PayeeType): Flow<List<Payee>> {
        return payeeDao.getRecentPayeesByType(userId, payeeType).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPayeeById(payeeId: Long): Payee? {
        return payeeDao.getPayeeById(payeeId)?.toDomain()
    }

    override suspend fun insertPayee(payee: Payee, userId: Long): Long {
        return payeeDao.insertPayee(payee.toEntity(userId))
    }

    override suspend fun updatePayee(payee: Payee, userId: Long) {
        payeeDao.updatePayee(payee.toEntity(userId))
    }

    override suspend fun deletePayee(payee: Payee, userId: Long) {
        payeeDao.deletePayee(payee.toEntity(userId))
    }

    override suspend fun getPayeeByNameAndType(name: String, payeeType: PayeeType, accountId: Long): Payee? {
        return payeeDao.getPayeeByNameAndType(name, payeeType, accountId)?.toDomain()
    }

    override fun searchPayeesByType(
        userId: Long,
        searchQuery: String,
        payeeType: PayeeType
    ): Flow<List<Payee>> {
        return payeeDao.searchPayeesByType(userId, searchQuery, payeeType).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}