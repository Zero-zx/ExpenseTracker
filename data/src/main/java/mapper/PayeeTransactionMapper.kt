package mapper

import model.PayeeEntity
import transaction.model.Payee

internal fun Payee.toEntity(): PayeeEntity {
    return PayeeEntity(
        id = id,
        name = name,
        userId = userId,
        isFromContacts = isFromContacts,
        contactId = contactId
    )
}

internal fun PayeeEntity.toDomain(): Payee {
    return Payee(
        id = id,
        name = name,
        userId = userId,
        isFromContacts = isFromContacts,
        contactId = contactId
    )
}


