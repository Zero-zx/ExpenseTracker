package payee.usecase

import payee.model.Payee
import session.repository.SessionRepository
import transaction.repository.PayeeRepository
import javax.inject.Inject

class AddPayeeUseCase @Inject constructor(
    private val repository: PayeeRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        payee: Payee
    ): Payee {
        require(payee.name.isNotBlank()) { "Payee name cannot be blank" }
        val userId = sessionRepository.getCurrentUserId()
        // Check if payee already exists
        val existingPayee = repository.getPayeeByName(payee.name, userId)
        if (existingPayee != null) {
            return existingPayee
        }
        val payeeId = repository.insertPayee(payee, userId)
        // Return payee with the generated ID
        return payee.copy(id = payeeId)
    }
}