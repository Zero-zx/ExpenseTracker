package payee.usecase

import session.repository.SessionRepository
import transaction.repository.PayeeRepository
import javax.inject.Inject

class DeletePayeeUseCase @Inject constructor(
    private val repository: PayeeRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(payeeId: Long) {
        val payee = repository.getPayeeById(payeeId)
        if (payee != null) {
            val userId = sessionRepository.getCurrentUserId()
            repository.deletePayee(payee, userId)
        }
    }
}

