package repository

import account.model.Account
import account.repository.AccountRepository
import dao.AccountDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import javax.inject.Inject

internal class AccountRepositoryImpl @Inject constructor(private val accountDao: AccountDao) :
    AccountRepository {
    override suspend fun initializeAdmin(account: Account) {
        val existingAccount = accountDao.getAccountByUsername(account.username)
        if (existingAccount == null) {
            accountDao.insert(account.toEntity())
        }
    }

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertAccount(account: Account): Long {
        return accountDao.insert(account.toEntity())
    }
}