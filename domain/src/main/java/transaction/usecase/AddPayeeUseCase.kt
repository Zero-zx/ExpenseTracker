package transaction.usecase

import session.repository.SessionRepository
import transaction.model.Payee
import transaction.repository.PayeeRepository
import javax.inject.Inject

class AddPayeeUseCase @Inject constructor(
    private val repository: PayeeRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        name: String,
        isFromContacts: Boolean = false,
        contactId: Long? = null
    ): Payee {
        require(name.isNotBlank()) { "Payee name cannot be blank" }
        val userId = sessionRepository.getCurrentUserId()
        // Check if payee already exists
        val existingPayee = repository.getPayeeByName(name, userId)
        if (existingPayee != null) {
            return existingPayee
        }

        val payee = Payee(
            name = name,
            userId = userId,
            isFromContacts = isFromContacts,
            contactId = contactId
        )

        val payeeId = repository.insertPayee(payee)
        // Return payee with the generated ID
        return payee.copy(id = payeeId)
    }
}