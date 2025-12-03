package model

import android.R
import androidx.room.Embedded
import androidx.room.Relation
import transaction.model.Transaction

// Model used for query one-to-many relationship
internal data class TransactionWithDetails(
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
    @Relation(
        parentColumn = "event_id",
        entityColumn = "id"
    )
    val eventEntity: EventEntity?,
//    @Relation(
//        parentColumn = "partner_id",
//        entityColumn = "id"
//    )
//    val partner: PartnerEntity?
)