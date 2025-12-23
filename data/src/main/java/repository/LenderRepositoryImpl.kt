package repository

import dao.LenderDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import transaction.model.Lender
import transaction.repository.LenderRepository
import javax.inject.Inject

internal class LenderRepositoryImpl @Inject constructor(
    private val lenderDao: LenderDao
) : LenderRepository {

    override fun getAllLendersByAccount(accountId: Long): Flow<List<Lender>> {
        return lenderDao.getAllLendersByAccount(accountId).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun insertLender(lender: Lender): Long {
        return lenderDao.insertLender(mapper.toEntity(lender))
    }

    override suspend fun updateLender(lender: Lender) {
        lenderDao.updateLender(mapper.toEntity(lender))
    }

    override suspend fun deleteLender(lender: Lender) {
        lenderDao.deleteLender(mapper.toEntity(lender))
    }

    override suspend fun getLenderById(lenderId: Long): Lender? {
        return lenderDao.getLenderById(lenderId)?.let { mapper.toDomain(it) }
    }

    override suspend fun getLenderByName(name: String, accountId: Long): Lender? {
        return lenderDao.getLenderByName(name, accountId)?.let { mapper.toDomain(it) }
    }

    override fun searchLendersByAccount(accountId: Long, searchQuery: String): Flow<List<Lender>> {
        return lenderDao.searchLendersByAccount(accountId, searchQuery).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    private object mapper {
        fun toEntity(lender: Lender): model.LenderEntity {
            return model.LenderEntity(
                id = lender.id,
                name = lender.name,
                phoneNumber = lender.phoneNumber,
                email = lender.email,
                accountId = lender.accountId,
                notes = lender.notes
            )
        }

        fun toDomain(entity: model.LenderEntity): Lender {
            return Lender(
                id = entity.id,
                name = entity.name,
                phoneNumber = entity.phoneNumber,
                email = entity.email,
                accountId = entity.accountId,
                notes = entity.notes
            )
        }
    }
}
