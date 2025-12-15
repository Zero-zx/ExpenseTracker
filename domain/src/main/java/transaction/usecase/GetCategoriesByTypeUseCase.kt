package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Category
import transaction.model.CategoryType
import transaction.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: CategoryType): Flow<List<Category>> {
        return categoryRepository.getCategoriesByType(type)
    }
}