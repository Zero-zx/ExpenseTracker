package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.AccountEntity

@Dao
internal interface AccountDao {
    @Query("SELECT * FROM tb_account WHERE username = :username")
    fun getAccountByUsername(username: String): AccountEntity?

    @Query("SELECT * FROM tb_account ORDER BY createAt DESC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Query("SELECT * FROM tb_account WHERE id = :accountId")
    fun getAccountById(accountId: Long): AccountEntity?
}
