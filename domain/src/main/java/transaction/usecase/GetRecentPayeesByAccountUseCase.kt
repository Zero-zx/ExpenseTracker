package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Payee
import transaction.repository.PayeeRepository
import javax.inject.Inject

class GetRecentPayeesByAccountUseCase @Inject constructor(
    private val repository: PayeeRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Payee>> {
        return repository.getRecentPayeesByAccount(accountId)
    }
}