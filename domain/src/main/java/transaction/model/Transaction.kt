package transaction.model

import account.model.Account
import category.model.Category
import payee.model.Payee

// Transaction model for domain layer
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val createAt: Long,
    val category: Category,
    val description: String?,
    val account: Account,
    val event: Event?,
    val location: Location? = null,
    val payees: List<Payee> = emptyList(),
    val images: TransactionImage? = null,
    val borrower: Payee? = null,
    val lender: Payee? = null
)



