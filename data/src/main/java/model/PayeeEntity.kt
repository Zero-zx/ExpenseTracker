package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import payee.model.PayeeType

/**
 * Entity for Payee/Payer in Transactions
 * Represents a person who receives or pays money in a transaction
 */
@Entity(
    tableName = "tb_payee",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
internal data class PayeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "userId")
    val userId: Long,
    @ColumnInfo(name = "isFromContacts")
    val isFromContacts: Boolean = false,
    @ColumnInfo(name = "contactId")
    val contactId: Long? = null,
    @ColumnInfo(name = "payeeType")
    val payeeType: PayeeType
)

