package mapper

import model.TransactionEntity
import model.TransactionWithDetails
import model.toDomain
import transaction.model.Transaction

internal fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        userId = account.userId,
        amount = amount,
        createAt = createAt,
        description = description!!,
        categoryId = category.id,
        accountId = account.id,
        eventId = event?.id,
        locationId = location?.id,
        borrowerId = borrower?.id,
        lenderId = lender?.id
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
        createAt = transactionEntity.createAt,
        location = locationEntity?.toDomain(),
        payees = payees.map { it.toDomain() },
        borrower = borrowerEntity?.toDomain(),
        lender = lenderEntity?.toDomain()

    )
}