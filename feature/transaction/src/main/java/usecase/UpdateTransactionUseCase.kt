package usecase

import account.model.Account
import category.model.Category
import transaction.model.Event
import transaction.model.Location
import payee.model.Payee
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
        payees: List<Payee> = emptyList(),
        borrower: Payee? = null,
        lender: Payee? = null
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
            payees = payees,
            borrower = borrower,
            lender = lender
        )
        repository.updateTransaction(transaction)
    }
}
