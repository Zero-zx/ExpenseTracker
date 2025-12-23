package usecase

import account.model.Account
import transaction.model.Borrower
import transaction.model.Category
import transaction.model.Event
import transaction.model.Lender
import transaction.model.Location
import transaction.model.Payee
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        amount: Double,
        category: Category,
        description: String?,
        account: Account,
        event: Event? = null,
        createAt: Long,
        location: Location? = null,
        payees: List<Payee> = emptyList(),
        borrower: Borrower? = null,
        lender: Lender? = null
    ): Long {
        val transaction = Transaction(
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
        return transactionRepository.insertTransactionWithPayees(transaction)
    }
}


