package category.usecase

import category.model.Category
import category.model.CategoryType
import transaction.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(type: CategoryType): Category {
        return categoryRepository.getCategoryByType(type)
    }
}