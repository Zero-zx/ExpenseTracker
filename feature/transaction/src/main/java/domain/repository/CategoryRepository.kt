package domain.repository

import data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun initializeCategories()
    fun getAllCategory(): Flow<List<Category>>
}