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

    @Query("SELECT * FROM tb_category WHERE type IN (:types)")
    fun getCategoriesByTypes(types: List<CategoryType>): Flow<List<CategoryEntity>>

    @Query(
        """
        SELECT DISTINCT c.* 
        FROM tb_category c
        INNER JOIN tb_transaction t ON c.id = t.categoryId
        WHERE c.type IN (:types)
        ORDER BY c.title ASC
    """
    )
    suspend fun getCategoriesUsedByType(
        types: List<CategoryType>
    ): List<CategoryEntity>

    @Query("SELECT * FROM tb_category WHERE type = :type")
    suspend fun getCategoryByType(type: CategoryType): CategoryEntity

    @Query("SELECT * FROM tb_category WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity

    @Query("SELECT * FROM tb_category WHERE type = :type AND title LIKE '%' || :searchQuery || '%'")
    fun searchCategoriesByType(searchQuery: String, type: CategoryType): Flow<List<CategoryEntity>>

}