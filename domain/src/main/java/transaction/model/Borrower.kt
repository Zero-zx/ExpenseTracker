package transaction.model

data class Borrower(
    val id: Long = 0,
    val name: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val accountId: Long,
    val notes: String? = null
)


