package repository

import category.model.Category
import category.model.CategoryType
import dao.CategoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import model.InitCategory
import model.toDomain
import transaction.repository.CategoryRepository
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

    override fun getCategoriesByType(type: CategoryType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type).map { list ->
            list.map { it ->
                it.toDomain()
            }
        }
    }

    override fun getCategoriesByTypes(types: List<CategoryType>): Flow<List<Category>> {
        return categoryDao.getCategoriesByTypes(types).map { list ->
            list.map { it ->
                it.toDomain()
            }
        }
    }

    override suspend fun getCategoryByType(type: CategoryType): Category {
        return categoryDao.getCategoryByType(type).toDomain()
    }

    override suspend fun getCategoriesUsedByType(types: List<CategoryType>): List<Category> {
        return categoryDao.getCategoriesUsedByType(types).map { it.toDomain() }
    }

    override suspend fun getCategoryById(id: Long): Category {
        return categoryDao.getCategoryById(id).toDomain()
    }

    override fun searchCategoriesByType(
        searchQuery: String,
        type: CategoryType
    ): Flow<List<Category>> {
        return categoryDao.searchCategoriesByType(searchQuery, type).map { list ->
            list.map { it ->
                it.toDomain()
            }
        }
    }
}
