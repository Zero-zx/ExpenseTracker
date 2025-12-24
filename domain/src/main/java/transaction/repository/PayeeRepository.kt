package transaction.repository

import kotlinx.coroutines.flow.Flow
import payee.model.Payee
import payee.model.PayeeType

interface PayeeRepository {
    fun getAllPayeesByType(userId: Long, payeeType: PayeeType): Flow<List<Payee>>
    fun getRecentPayeesByType(userId: Long, payeeType: PayeeType): Flow<List<Payee>>
    suspend fun getPayeeById(payeeId: Long): Payee?
    suspend fun insertPayee(payee: Payee, userId: Long): Long
    suspend fun updatePayee(payee: Payee, userId: Long)
    suspend fun deletePayee(payee: Payee, userId: Long)
    suspend fun getPayeeByName(name: String, accountId: Long): Payee?
    fun searchPayeesByType(
        userId: Long,
        searchQuery: String,
        payeeType: PayeeType
    ): Flow<List<Payee>>
}