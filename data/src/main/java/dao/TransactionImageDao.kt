package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.TransactionImageEntity

@Dao
interface TransactionImageDao {
    @Query("SELECT * FROM transaction_images WHERE transaction_id = :transactionId")
    fun getImagesForTransaction(transactionId: Long): Flow<List<TransactionImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: TransactionImageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<TransactionImageEntity>): List<Long>

    @Query("DELETE FROM transaction_images WHERE id = :imageId")
    suspend fun deleteImage(imageId: Long)

    @Query("DELETE FROM transaction_images WHERE transaction_id = :transactionId")
    suspend fun deleteImagesForTransaction(transactionId: Long)
}