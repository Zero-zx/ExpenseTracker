package mapper

import data.model.Account
import model.AccountEntity

internal fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        username = username,
        type = type,
        balance = balance,
        createAt = createAt
    )
}