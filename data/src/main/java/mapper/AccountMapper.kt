package mapper

import account.model.Account
import model.AccountEntity

internal fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        userId = userId,
        username = username,
        type = type,
        balance = balance,
        createAt = createAt
    )
}

internal fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        userId = userId,
        username = username,
        type = type,
        balance = balance,
        createAt = createAt
    )
}