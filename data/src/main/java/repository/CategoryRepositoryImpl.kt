package repository

import dao.CategoryDao
import transaction.model.Category
import transaction.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import model.InitCategory
import model.toDomain
import javax.inject.Inject

internal class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override suspend fun initializeCategories() {
        val existingCategories = categoryDao.getExistingCategory()
        if (existingCategories.isEmpty()) {
            InitCategory.CATEGORY_LIST.forEach {
                categoryDao.insert(it)
            }
        }
    }

    override fun getAllCategory(): Flow<List<Category>> {
        return categoryDao.getAllCategory().map { list ->
            list.map { it ->
                it.toDomain()
            }
        }
    }
}
