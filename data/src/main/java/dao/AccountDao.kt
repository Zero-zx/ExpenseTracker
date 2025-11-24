package dao

import androidx.room.Dao
import androidx.room.Query
import model.AccountEntity

@Dao
interface AccountDao {
    @Query("SELECT * FROM tb_account WHERE username = :username")
    fun getAccountByUsername(username: String): AccountEntity?
}
