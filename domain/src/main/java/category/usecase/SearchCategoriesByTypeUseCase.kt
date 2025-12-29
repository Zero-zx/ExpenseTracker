package category.usecase

import category.model.Category
import category.model.CategoryType
import kotlinx.coroutines.flow.Flow
import transaction.repository.CategoryRepository
import javax.inject.Inject

class SearchCategoriesByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(searchQuery: String, type: CategoryType): Flow<List<Category>> {
        return categoryRepository.searchCategoriesByType(searchQuery, type)

    }
}