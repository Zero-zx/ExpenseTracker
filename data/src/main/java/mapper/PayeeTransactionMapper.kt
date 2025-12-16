package mapper

import model.PayeeEntity
import transaction.model.PayeeTransaction

internal fun PayeeTransaction.toEntity(): PayeeEntity {
    return PayeeEntity(
        id = id,
        name = name,
        accountId = accountId,
        isFromContacts = isFromContacts,
        contactId = contactId
    )
}

internal fun PayeeEntity.toDomain(): PayeeTransaction {
    return PayeeTransaction(
        id = id,
        name = name,
        accountId = accountId,
        isFromContacts = isFromContacts,
        contactId = contactId
    )
}


