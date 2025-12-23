package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Lender

interface LenderRepository {
    fun getAllLendersByAccount(accountId: Long): Flow<List<Lender>>

    suspend fun insertLender(lender: Lender): Long

    suspend fun updateLender(lender: Lender)

    suspend fun deleteLender(lender: Lender)

    suspend fun getLenderById(lenderId: Long): Lender?

    suspend fun getLenderByName(name: String, accountId: Long): Lender?

    fun searchLendersByAccount(accountId: Long, searchQuery: String): Flow<List<Lender>>
}

