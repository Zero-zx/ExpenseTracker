package model

import account.model.AccountType
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Account model for entities layer
@Entity(
    tableName = "tb_account",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
internal data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "userId")
    val userId: Long,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "type")
    val type: AccountType,
    @ColumnInfo(name = "balance")
    val balance: Double,
    @ColumnInfo(name = "createAt")
    val createAt: Long
)