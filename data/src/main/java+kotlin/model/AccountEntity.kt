package model

import AccountType
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_account")
internal data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "type")
    val type: AccountType,
    @ColumnInfo(name = "balance")
    val balance: Double,
    @ColumnInfo(name = "createAt")
    val createAt: Long
)