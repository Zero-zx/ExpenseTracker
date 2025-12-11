package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.PayeeTransaction
import transaction.repository.PayeeTransactionRepository
import javax.inject.Inject

class GetPayeesByAccountUseCase @Inject constructor(
    private val repository: PayeeTransactionRepository
) {
    operator fun invoke(accountId: Long): Flow<List<PayeeTransaction>> {
        return repository.getAllPayeesByAccount(accountId)
    }
}


