package transaction.usecase

import transaction.model.Category
import transaction.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Long): Category {
        return categoryRepository.getCategoryById(id)
    }
}