package transaction.repository

import kotlinx.coroutines.flow.Flow
import transaction.model.Category

interface CategoryRepository {
    suspend fun initializeCategories()
    fun getAllCategory(): Flow<List<Category>>
}



