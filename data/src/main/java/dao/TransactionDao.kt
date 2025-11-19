import androidx.room.Dao
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction`")
    fun getAllTransactions(): List<TransactionEntity>
}