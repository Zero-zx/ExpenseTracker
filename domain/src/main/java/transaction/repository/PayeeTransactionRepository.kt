package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.PayeeTransaction

interface PayeeTransactionRepository {
    fun getAllPayeesByAccount(accountId: Long): Flow<List<PayeeTransaction>>
    fun getRecentPayeesByAccount(accountId: Long): Flow<List<PayeeTransaction>>
    suspend fun getPayeeById(payeeId: Long): PayeeTransaction?
    suspend fun insertPayee(payee: PayeeTransaction): Long
    suspend fun updatePayee(payee: PayeeTransaction)
    suspend fun deletePayee(payee: PayeeTransaction)
    suspend fun getPayeeByName(name: String, accountId: Long): PayeeTransaction?
}