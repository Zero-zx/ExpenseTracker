package domain.usecase

import domain.repository.CategoryRepository
import javax.inject.Inject

class InitializeCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() {
        categoryRepository.initializeCategories()
    }
}