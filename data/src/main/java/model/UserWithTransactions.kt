package model

import AccountEntity
import TransactionEntity
import androidx.room.Embedded
import androidx.room.Relation

data class UserWithTransactions(
    @Embedded val account: AccountEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "accountId"
    )
    val transactions: List<TransactionEntity>
)
