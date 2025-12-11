package mapper

import model.PayeeTransactionEntity
import transaction.model.PayeeTransaction

internal fun PayeeTransaction.toEntity(): PayeeTransactionEntity {
    return PayeeTransactionEntity(
        id = id,
        name = name,
        accountId = accountId,
        isFromContacts = isFromContacts,
        contactId = contactId
    )
}

internal fun PayeeTransactionEntity.toDomain(): PayeeTransaction {
    return PayeeTransaction(
        id = id,
        name = name,
        accountId = accountId,
        isFromContacts = isFromContacts,
        contactId = contactId
    )
}


