package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Category
import transaction.model.CategoryType

interface CategoryRepository {
    suspend fun initializeCategories()
    fun getAllCategory(): Flow<List<Category>>
    fun getCategoriesByType(type: CategoryType): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category
}



