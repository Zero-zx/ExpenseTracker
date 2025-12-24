package category.usecase

import category.model.Category
import transaction.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Long): Category {
        return categoryRepository.getCategoryById(id)
    }
}