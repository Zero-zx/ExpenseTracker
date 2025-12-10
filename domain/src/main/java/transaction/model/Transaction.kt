package transaction.model

import account.model.Account

// Transaction model for domain layer
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val createAt: Long,
    val category: Category,
    val description: String?,
    val account: Account,
    val event: Event?,
    val partnerId: Long,
    val location: Location? = null,
    val payeeIds: List<Long> = emptyList(),
    val images: TransactionImage? = null
)



