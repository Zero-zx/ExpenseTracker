import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.UserWithTransactions

@Dao
interface TransactionDao {
    @Query("SELECT * FROM tb_transaction WHERE accountId = :accountId")
    fun getAccountWithTransactions(accountId: Long): Flow<UserWithTransactions?>
}