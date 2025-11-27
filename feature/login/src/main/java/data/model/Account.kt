package data.model

data class Account(
    val id: Long = 0,
    val username: String,
    val type: AccountType,
    val balance: Double,
    val createAt: Long
)
