package repository

import dao.AccountDao
import data.model.Account
import domain.repository.AccountRepository
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
}