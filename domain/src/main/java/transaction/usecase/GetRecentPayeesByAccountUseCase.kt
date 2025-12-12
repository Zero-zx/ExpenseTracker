package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.PayeeTransaction
import transaction.repository.PayeeTransactionRepository
import javax.inject.Inject

class GetRecentPayeesByAccountUseCase @Inject constructor(
    private val repository: PayeeTransactionRepository
) {
    operator fun invoke(accountId: Long): Flow<List<PayeeTransaction>> {
        return repository.getRecentPayeesByAccount(accountId)
    }
}