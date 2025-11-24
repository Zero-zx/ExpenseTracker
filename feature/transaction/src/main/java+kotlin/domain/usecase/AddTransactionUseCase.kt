package domain.usecase

import data.model.Category
import data.model.Transaction
import domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        amount: Double,
        category: Category,
        description: String?
    ): Long {
        val transaction = Transaction(
            amount = amount,
            createAt = System.currentTimeMillis(),
            category = category,
            description = description,
            accountId = 0,
            eventId = 0,
            partnerId = 0
        )
        require(transaction.amount > 0) { "Amount must be greater than 0" }
        require(transaction.description.isNotBlank()) { "Description cannot be blank" }

        return repository.insertTransaction(transaction)
    }
}