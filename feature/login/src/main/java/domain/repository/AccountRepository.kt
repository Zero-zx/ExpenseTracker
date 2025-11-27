package domain.repository

import data.model.Account


interface AccountRepository {
    suspend fun initializeAdmin(account: Account)
}