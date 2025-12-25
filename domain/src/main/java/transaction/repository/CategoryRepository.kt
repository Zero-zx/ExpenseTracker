package transaction.repository

import category.model.Category
import category.model.CategoryType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun initializeCategories()
    fun getAllCategory(): Flow<List<Category>>
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    fun getCategoriesByTypes(types: List<CategoryType>): Flow<List<Category>>
    suspend fun getCategoryByType(type: CategoryType): Category
    suspend fun getCategoryById(id: Long): Category
    fun searchCategoriesByType(searchQuery: String, type: CategoryType): Flow<List<Category>>
}