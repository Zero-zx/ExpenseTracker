package model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

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
    @Relation(
        parentColumn = "location_id",
        entityColumn = "id"
    )
    val locationEntity: LocationEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TransactionPayeeEntity::class,
            parentColumn = "transactionId",
            entityColumn = "payeeId"
        )
    )
    val payees: List<PayeeEntity>,
    @Relation(
        parentColumn = "borrower_id",
        entityColumn = "id",
    )
    val borrowerEntity: PayeeEntity?,
    @Relation(
        parentColumn = "lender_id",
        entityColumn = "id",
    )
    val lenderEntity: PayeeEntity?
)