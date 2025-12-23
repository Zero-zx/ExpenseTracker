package mapper

import model.BorrowerEntity
import transaction.model.Borrower

internal fun Borrower.toEntity(): BorrowerEntity {
    return BorrowerEntity(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email,
        accountId = accountId,
        notes = notes
    )
}

internal fun BorrowerEntity.toDomain(): Borrower {
    return Borrower(
        id = id,
        name = name,
        phoneNumber = phoneNumber,
        email = email,
        accountId = accountId,
        notes = notes
    )
}