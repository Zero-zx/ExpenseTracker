package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tb_user")
internal data class UserEntity(
    @PrimaryKey
    val id: Long = 1L,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long,
    @ColumnInfo(name = "createdAt")
    val createdAt: Long,
    @ColumnInfo(name = "firebaseUid")
    val firebaseUid: String?,
    @ColumnInfo(name = "email")
    val email: String?
)