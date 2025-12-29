package category.usecase

import category.model.Category
import category.model.CategoryType
import kotlinx.coroutines.flow.Flow
import transaction.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: CategoryType): Flow<List<Category>> {
        when (type) {
            CategoryType.EXPENSE, CategoryType.INCOME -> {
                return categoryRepository.getCategoriesByType(type)
            }

            else -> {
                return categoryRepository.getCategoriesByTypes(
                    listOf(
                        CategoryType.LEND,
                        CategoryType.COLLECT_DEBT,
                        CategoryType.BORROWING,
                        CategoryType.REPAYMENT
                    )
                )
            }
        }
    }
}