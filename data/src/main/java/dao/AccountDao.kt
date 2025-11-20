import android.accounts.Account
import androidx.room.Dao
import androidx.room.Query

@Dao
interface AccountDao {
    @Query("SELECT * FROM tb_account WHERE username = :username")
    fun getAccountByUsername(username: String): Account?
}