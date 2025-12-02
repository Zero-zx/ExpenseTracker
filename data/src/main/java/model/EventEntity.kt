package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tb_event",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("accountId")]
)
internal data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "eventName")
    val eventName: String,
    @ColumnInfo(name = "startDate")
    val startDate: Long,
    @ColumnInfo(name = "endDate")
    val endDate: Long?,
    @ColumnInfo(name = "numberOfParticipants")
    val numberOfParticipants: Int,
    @ColumnInfo(name = "accountId")
    val accountId: Long,
    @ColumnInfo(name = "isActive")
    val isActive: Boolean = true
)
