package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Payee
import transaction.repository.PayeeRepository
import javax.inject.Inject

class GetPayeesByAccountUseCase @Inject constructor(
    private val repository: PayeeRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Payee>> {
        return repository.getAllPayeesByAccount(accountId)
    }
}