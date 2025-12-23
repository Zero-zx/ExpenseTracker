package transaction.model

data class Lender(
    val id: Long = 0,
    val name: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val accountId: Long,
    val notes: String? = null
)

