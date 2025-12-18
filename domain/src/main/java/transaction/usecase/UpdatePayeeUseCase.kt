package transaction.usecase

import transaction.model.PayeeTransaction
import transaction.repository.PayeeTransactionRepository
import javax.inject.Inject

class UpdatePayeeUseCase @Inject constructor(
    private val repository: PayeeTransactionRepository
) {
    suspend operator fun invoke(payee: PayeeTransaction) {
        "Payee name cannot be blank"
        require(payee.name.isNotBlank()) {
        }
        repository.updatePayee(payee)
    }
}


