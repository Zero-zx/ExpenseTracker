package dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import category.model.CategoryType
import kotlinx.coroutines.flow.Flow
import model.CategoryEntity

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

    @Query("SELECT * FROM tb_category WHERE type = :type AND title LIKE '%' || :searchQuery || '%'")
    fun searchCategoriesByType(searchQuery: String, type: CategoryType): Flow<List<CategoryEntity>>

}