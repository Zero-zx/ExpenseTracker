package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tb_transaction",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("accountId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("categoryId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("eventId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = PayeeEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("borrowerId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = PayeeEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("lenderId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_NULL
        )],
    indices = [
        Index(value = ["accountId"]),
        Index(value = ["categoryId"]),
        Index(value = ["eventId"]),
        Index(value = ["borrowerId"]),
        Index(value = ["lenderId"])
    ]
)

// Transaction model for entities layer
internal data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "userId")
    val userId: Long,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "create_at")
    val createAt: Long,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "categoryId")
    val categoryId: Long,
    @ColumnInfo(name = "accountId")
    val accountId: Long,
    @ColumnInfo(name = "eventId")
    val eventId: Long?,
    @ColumnInfo(name = "locationId")
    val locationId: Long?,
    @ColumnInfo(name = "borrowerId")
    val borrowerId: Long?,
    @ColumnInfo(name = "lenderId")
    val lenderId: Long?
)