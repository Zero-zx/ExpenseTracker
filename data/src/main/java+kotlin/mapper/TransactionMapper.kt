package mapper

import data.model.Transaction
import model.TransactionEntity
import model.TransactionWithDetails
import model.toCategory

internal fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        createAt = createAt,
        description = description,
        categoryId = category.id,
        accountId = accountId,
        eventId = eventId,
        partnerId = partnerId,
    )
}

internal fun TransactionWithDetails.toDomain(): Transaction {
    return Transaction(
        id = transactionEntity.id,
        description = transactionEntity.description,
        amount = transactionEntity.amount,
        category = categoryEntity.toCategory(),
        accountId = 1,
        eventId = 1,
        partnerId = 1,
        createAt = transactionEntity.createAt,
    )
}