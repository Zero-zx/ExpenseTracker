package payee.usecase

import payee.model.Payee
import session.repository.SessionRepository
import transaction.repository.PayeeRepository
import javax.inject.Inject

class UpdatePayeeUseCase @Inject constructor(
    private val repository: PayeeRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(payee: Payee) {
        "Payee name cannot be blank"
        require(payee.name.isNotBlank()) {
        }
        val userId = sessionRepository.getCurrentUserId()
        repository.updatePayee(payee, userId)
    }
}


