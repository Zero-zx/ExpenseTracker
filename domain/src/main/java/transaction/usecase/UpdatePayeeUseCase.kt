package transaction.usecase

import transaction.model.Payee
import transaction.repository.PayeeRepository
import javax.inject.Inject

class UpdatePayeeUseCase @Inject constructor(
    private val repository: PayeeRepository
) {
    suspend operator fun invoke(payee: Payee) {
        "Payee name cannot be blank"
        require(payee.name.isNotBlank()) {
        }
        repository.updatePayee(payee)
    }
}


