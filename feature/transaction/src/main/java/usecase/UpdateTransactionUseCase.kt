package usecase

import account.model.Account
import transaction.model.Category
import transaction.model.Event
import transaction.model.Location
import transaction.model.Payee
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        transactionId: Long,
        amount: Double,
        category: Category,
        description: String?,
        account: Account,
        event: Event? = null,
        createAt: Long,
        location: Location? = null,
        payees: List<Payee> = emptyList()
    ) {
        val transaction = Transaction(
            id = transactionId,
            amount = amount,
            createAt = createAt,
            category = category,
            description = description,
            account = account,
            event = event,
            location = location,
            payees = payees
        )
        repository.updateTransaction(transaction)
    }
}
