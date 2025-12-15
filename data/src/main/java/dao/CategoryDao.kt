package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.CategoryEntity
import transaction.model.CategoryType

@Dao
internal interface CategoryDao {
    @Query("SELECT * FROM tb_category")
    fun getAllCategory(): Flow<List<CategoryEntity>>

     @Query("SELECT * FROM tb_category")
    fun getExistingCategory(): List<CategoryEntity>

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM tb_category WHERE type = :type")
    fun getCategoriesByType(type: CategoryType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM tb_category WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity
}