package model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Model used for query one-to-many relationship
internal data class TransactionWithDetails(
    @Embedded val transactionEntity: TransactionEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "id"
    )
    val accountEntity: AccountEntity,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "id"
    )
    val eventEntity: EventEntity?,
    @Relation(
        parentColumn = "locationId",
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
        parentColumn = "borrowerId",
        entityColumn = "id",
    )
    val borrowerEntity: PayeeEntity?,
    @Relation(
        parentColumn = "lenderId",
        entityColumn = "id",
    )
    val lenderEntity: PayeeEntity?
)