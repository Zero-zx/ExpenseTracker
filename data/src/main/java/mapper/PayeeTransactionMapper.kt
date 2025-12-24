package mapper

import model.PayeeEntity
import payee.model.Payee

internal fun Payee.toEntity(userId: Long): PayeeEntity {
    return PayeeEntity(
        id = id,
        name = name,
        userId = userId,
        isFromContacts = isFromContacts,
        contactId = contactId,
        payeeType = payeeType
    )
}

internal fun PayeeEntity.toDomain(): Payee {
    return Payee(
        id = id,
        name = name,
        isFromContacts = isFromContacts,
        contactId = contactId,
        payeeType = payeeType
    )
}


