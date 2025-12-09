package transaction.model

/**
 * Payee model for transactions (separate from Event Payees)
 * Represents a person/entity who paid or received money in a transaction
 */
data class PayeeTransaction(
    val id: Long = 0,
    val name: String,
    val accountId: Long, // Account that owns this payee
    val isFromContacts: Boolean = false, // Whether this payee is from device contacts
    val contactId: Long? = null // Contact ID if from device contacts
)

