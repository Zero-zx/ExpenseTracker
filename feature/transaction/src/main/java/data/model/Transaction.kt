package data.model

data class Transaction(
    val id: Long,
    val amount: Double,
    val createAt: Long,
    val category: Category,
    val description: String,
    val accountId: Long,
    val eventId: Long,
    val partnerId: Long
)