package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import model.AccountEntity

@Dao
internal interface AccountDao {
    @Query("SELECT * FROM tb_account WHERE username = :username")
    fun getAccountByUsername(username: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(account: AccountEntity)
}
