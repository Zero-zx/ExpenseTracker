package repository

import dao.BorrowerDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import transaction.model.Borrower
import transaction.repository.BorrowerRepository
import javax.inject.Inject

internal class BorrowerRepositoryImpl @Inject constructor(
    private val borrowerDao: BorrowerDao
) : BorrowerRepository {

    override fun getAllBorrowersByAccount(accountId: Long): Flow<List<Borrower>> {
        return borrowerDao.getAllBorrowersByAccount(accountId).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun insertBorrower(borrower: Borrower): Long {
        return borrowerDao.insertBorrower(mapper.toEntity(borrower))
    }

    override suspend fun updateBorrower(borrower: Borrower) {
        borrowerDao.updateBorrower(mapper.toEntity(borrower))
    }

    override suspend fun deleteBorrower(borrower: Borrower) {
        borrowerDao.deleteBorrower(mapper.toEntity(borrower))
    }

    override suspend fun getBorrowerById(borrowerId: Long): Borrower? {
        return borrowerDao.getBorrowerById(borrowerId)?.let { mapper.toDomain(it) }
    }

    override suspend fun getBorrowerByName(name: String, accountId: Long): Borrower? {
        return borrowerDao.getBorrowerByName(name, accountId)?.let { mapper.toDomain(it) }
    }

    override fun searchBorrowersByAccount(accountId: Long, searchQuery: String): Flow<List<Borrower>> {
        return borrowerDao.searchBorrowersByAccount(accountId, searchQuery).map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    private object mapper {
        fun toEntity(borrower: Borrower): model.BorrowerEntity {
            return model.BorrowerEntity(
                id = borrower.id,
                name = borrower.name,
                phoneNumber = borrower.phoneNumber,
                email = borrower.email,
                accountId = borrower.accountId,
                notes = borrower.notes
            )
        }

        fun toDomain(entity: model.BorrowerEntity): Borrower {
            return Borrower(
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
