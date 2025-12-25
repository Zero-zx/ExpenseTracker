package repository

import account.model.Account
import account.repository.AccountRepository
import dao.AccountDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import session.UserSessionManager
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val sessionManager: UserSessionManager
) : AccountRepository {
    override suspend fun initializeAdmin(account: Account) {
        val existingAccount = accountDao.getAccountByUsername(account.username)
        val userId = sessionManager.getCurrentUserId()

        if (existingAccount == null) {
            accountDao.insert(account.toEntity(userId))
        }
    }

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAccounts(): Flow<List<Account>> {
        val userId = sessionManager.getCurrentUserId()

        return accountDao.getAccountsByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAccountById(accountId: Long): Account? {
        return accountDao.getAccountById(accountId)?.toDomain()
    }

    override suspend fun insertAccount(account: Account): Long {
        val userId = sessionManager.getCurrentUserId()

        return accountDao.insert(account.toEntity(userId))
    }

    override suspend fun updateAccount(account: Account) {
        val userId = sessionManager.getCurrentUserId()

        return accountDao.update(account.toEntity(userId))
    }

    override suspend fun deleteAccount(account: Account) {
        val userId = sessionManager.getCurrentUserId()

        return accountDao.delete(account.toEntity(userId))
    }
}