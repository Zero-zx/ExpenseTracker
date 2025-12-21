package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Payee

interface PayeeRepository {
    fun getAllPayeesByAccount(accountId: Long): Flow<List<Payee>>
    fun getRecentPayeesByAccount(accountId: Long): Flow<List<Payee>>
    suspend fun getPayeeById(payeeId: Long): Payee?
    suspend fun insertPayee(payee: Payee): Long
    suspend fun updatePayee(payee: Payee)
    suspend fun deletePayee(payee: Payee)
    suspend fun getPayeeByName(name: String, accountId: Long): Payee?
}