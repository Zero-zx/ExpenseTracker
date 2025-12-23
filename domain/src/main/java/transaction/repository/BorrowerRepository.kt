package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Borrower

interface BorrowerRepository {
    fun getAllBorrowersByAccount(accountId: Long): Flow<List<Borrower>>
    suspend fun insertBorrower(borrower: Borrower): Long
    suspend fun updateBorrower(borrower: Borrower)
    suspend fun getBorrowerByName(name: String, accountId: Long): Borrower?
    suspend fun getBorrowerById(borrowerId: Long): Borrower?
    suspend fun deleteBorrower(borrower: Borrower)
    fun searchBorrowersByAccount(accountId: Long, searchQuery: String): Flow<List<Borrower>>
}


