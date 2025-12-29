package dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import model.AccountEntity

@Dao
internal interface AccountDao {
    @Query("SELECT * FROM tb_account WHERE username = :username")
    fun getAccountByUsername(username: String): AccountEntity?

    @Query("SELECT * FROM tb_account ORDER BY createAt DESC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM tb_account WHERE userId = :userId ORDER BY createAt DESC")
    fun getAccountsByUserId(userId: Long): Flow<List<AccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Query("SELECT * FROM tb_account WHERE id = :accountId")
    fun getAccountById(accountId: Long): AccountEntity?

    @Update
    suspend fun update(account: AccountEntity)

    @Query(
        """
        UPDATE tb_account 
        SET balance = balance + :amount
        WHERE id = :accountId 
        AND userId = :userId
        """
    )
    suspend fun updateBalance(accountId: Long, userId: Long, amount: Double) : Int


    @Delete
    suspend fun delete(account: AccountEntity)
}
