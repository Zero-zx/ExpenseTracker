package account.repository

import account.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun initializeAdmin(account: Account)

    fun getAllAccounts(): Flow<List<Account>>

    /**
     * Get all accounts for a specific user
     */
    fun getUserAccounts(userId: Long): Flow<List<Account>>

    suspend fun getAccountById(accountId: Long): Account?

    suspend fun insertAccount(account: Account): Long
}