package transaction.usecase

import transaction.model.PayeeTransaction
import transaction.repository.PayeeTransactionRepository
import javax.inject.Inject

class AddPayeeUseCase @Inject constructor(
    private val repository: PayeeTransactionRepository
) {
    suspend operator fun invoke(
        name: String,
        accountId: Long,
        isFromContacts: Boolean = false,
        contactId: Long? = null
    ): Long {
        require(name.isNotBlank()) { "Payee name cannot be blank" }

        // Check if payee already exists
        val existingPayee = repository.getPayeeByName(name, accountId)
        if (existingPayee != null) {
            return existingPayee.id
        }

        val payee = PayeeTransaction(
            name = name,
            accountId = accountId,
            isFromContacts = isFromContacts,
            contactId = contactId
        )

        return repository.insertPayee(payee)
    }
}


