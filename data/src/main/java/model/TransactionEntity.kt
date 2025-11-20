import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tb_transaction",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("accountId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "createAt")
    val createAt: Long,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "categoryId")
    val categoryId: Long,
    @ColumnInfo(name = "accountId")
    val accountId: Long,
    @ColumnInfo(name = "eventId")
    val eventId: Long,
    @ColumnInfo(name = "partnerId")
    val partnerId: Long
)