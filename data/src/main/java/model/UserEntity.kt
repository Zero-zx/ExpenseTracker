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
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "firebase_uid")
    val firebaseUid: String?,
    @ColumnInfo(name = "email")
    val email: String?
)