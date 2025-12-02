package mapper

import transaction.model.Transaction
import model.TransactionEntity
import model.TransactionWithDetails
import model.toDomain

internal fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        createAt = createAt,
        description = description!!,
        categoryId = category.id,
        accountId = account.id,
        eventId = event?.id,
        partnerId = partnerId,
    )
}

internal fun TransactionWithDetails.toDomain(): Transaction {
    return Transaction(
        id = transactionEntity.id,
        description = transactionEntity.description,
        amount = transactionEntity.amount,
        category = categoryEntity.toDomain(),
        account = accountEntity.toDomain(),
        event = eventEntity?.toDomain(),
        partnerId = 1,
        createAt = transactionEntity.createAt,
    )
}