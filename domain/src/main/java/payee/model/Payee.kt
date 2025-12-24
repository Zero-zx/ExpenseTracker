package payee.model

data class Payee(
    val id: Long = 0,
    val name: String,
    val isFromContacts: Boolean = false,
    val contactId: Long? = null,
    val payeeType: PayeeType
)

