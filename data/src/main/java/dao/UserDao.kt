package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.UserEntity

@Dao
internal interface UserDao {
    @Query("SELECT * FROM tb_user WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM tb_user")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM tb_user WHERE firebaseUid = :firebaseUid")
    suspend fun getUserByFirebaseUid(firebaseUid: String): UserEntity?
}

