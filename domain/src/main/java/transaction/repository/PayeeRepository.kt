package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Payee

interface PayeeRepository {
    fun getAllPayeesByAccount() : Flow<List<Payee>>
}