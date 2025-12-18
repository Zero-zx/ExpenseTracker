package transaction.usecase

import transaction.repository.PayeeTransactionRepository
import javax.inject.Inject

class DeletePayeeUseCase @Inject constructor(
    private val repository: PayeeTransactionRepository
) {
    suspend operator fun invoke(payeeId: Long) {
        val payee = repository.getPayeeById(payeeId)
        if (payee != null) {
            repository.deletePayee(payee)
        }
    }
}

