package usecase

import account.model.Account
import transaction.model.Category
import transaction.model.Event
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        amount: Double,
        category: Category,
        description: String?,
        account: Account,
        event: Event? = null,
        createAt: Long
    ): Long {
        val transaction = Transaction(
            amount = amount,
            createAt = createAt,
            category = category,
            description = description,
            account = account,
            event = event,
            partnerId = 0
        )
        require(transaction.amount > 0) { "Amount must be greater than 0" }
        require(transaction.description?.isNotBlank() == true) { "Description cannot be blank" }

        return repository.insertTransaction(transaction)
    }
}


