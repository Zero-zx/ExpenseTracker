package category.usecase

import kotlinx.coroutines.flow.Flow
import category.model.Category
import transaction.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getAllCategory()
    }
}



