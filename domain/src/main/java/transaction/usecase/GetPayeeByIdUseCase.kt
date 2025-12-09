package transaction.usecase

import transaction.model.PayeeTransaction
import transaction.repository.PayeeTransactionRepository
import javax.inject.Inject

class GetPayeeByIdUseCase @Inject constructor(
    private val repository: PayeeTransactionRepository
) {
    suspend operator fun invoke(payeeId: Long): PayeeTransaction? {
        return repository.getPayeeById(payeeId)
    }
}

