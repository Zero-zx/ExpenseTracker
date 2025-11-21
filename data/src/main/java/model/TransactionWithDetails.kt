package model

import android.R
import androidx.room.Embedded
import androidx.room.Relation
import data.model.Transaction

data class TransactionWithDetails(
    @Embedded val transactionEntity: TransactionEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity,
    @Relation(
        parentColumn = "account_id",
        entityColumn = "id"
    )
    val accountEntity: AccountEntity,
//    @Relation(
//        parentColumn = "event_id",
//        entityColumn = "id"
//    )
//    val event: EventEntity?,
//    @Relation(
//        parentColumn = "partner_id",
//        entityColumn = "id"
//    )
//    val partner: PartnerEntity?
)

fun TransactionWithDetails.toTransaction(): Transaction {
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