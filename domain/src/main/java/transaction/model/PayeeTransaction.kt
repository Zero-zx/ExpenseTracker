package transaction.model

data class PayeeTransaction(
    val id: Long = 0,
    val name: String,
    val accountId: Long, // Account that owns this payee
    val isFromContacts: Boolean = false, // Whether this payee is from device contacts
    val contactId: Long? = null // Contact ID if from device contacts
)