package transaction.usecase

import transaction.repository.PayeeRepository
import javax.inject.Inject

class DeletePayeeUseCase @Inject constructor(
    private val repository: PayeeRepository
) {
    suspend operator fun invoke(payeeId: Long) {
        val payee = repository.getPayeeById(payeeId)
        if (payee != null) {
            repository.deletePayee(payee)
        }
    }
}

