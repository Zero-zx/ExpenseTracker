package transaction.usecase

import transaction.model.Payee
import transaction.repository.PayeeRepository
import javax.inject.Inject

class GetPayeeByIdUseCase @Inject constructor(
    private val repository: PayeeRepository
) {
    suspend operator fun invoke(payeeId: Long): Payee? {
        return repository.getPayeeById(payeeId)
    }
}