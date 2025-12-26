package model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_images",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transactionId")]
)
data class TransactionImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "transactionId")
    val transactionId: Long,

    @ColumnInfo(name = "filePath")
    val filePath: String,

    @ColumnInfo(name = "fileName")
    val fileName: String,

    @ColumnInfo(name = "mimeType")
    val mimeType: String,

    @ColumnInfo(name = "fileSize")
    val fileSize: Long,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long
)
