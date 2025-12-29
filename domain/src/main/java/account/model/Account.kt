package account.model

/**
 * Account domain model
 * One User can have multiple Accounts
 * One Account contains many Transactions
 */
data class Account(
    val id: Long = 0,
    val username: String,
    val type: AccountType,
    val balance: Double,
    val createAt: Long
)
