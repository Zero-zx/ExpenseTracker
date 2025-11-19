import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "create_at")
    val createAt: Long,
    @ColumnInfo(name = "description")
    val description: String,
    val categoryId: Int,
    val accountId: Int,
    val eventId: Int,
    val partnerId: Int
)