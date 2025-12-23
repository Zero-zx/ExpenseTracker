package mapper

import model.LenderEntity
import transaction.model.Lender

internal fun Lender.toEntity(): LenderEntity {
    return LenderEntity(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email,
        accountId = accountId,
        notes = notes
    )
}

internal fun LenderEntity.toDomain(): Lender {
    return Lender(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email,
        accountId = accountId,
        notes = notes
    )
}

