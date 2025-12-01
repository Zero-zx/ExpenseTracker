package transaction.model

// Transaction model for domain layer
data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val createAt: Long,
    val category: Category,
    val description: String?,
    val accountId: Long,
    val eventId: Long,
    val partnerId: Long
)


