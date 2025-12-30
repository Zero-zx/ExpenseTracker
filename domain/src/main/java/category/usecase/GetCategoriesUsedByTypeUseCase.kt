package category.usecase

import category.model.Category
import category.model.CategoryType
import transaction.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case to get categories for report/analysis screens
 * - EXPENSE -> returns EXPENSE, LEND, REPAYMENT
 * - INCOME -> returns INCOME, BORROWING, COLLECT_DEBT
 */
class GetCategoriesUsedByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(type: CategoryType): List<Category> {
        return when (type) {
            // For expense analysis: EXPENSE, LEND, REPAYMENT
            CategoryType.EXPENSE -> {
                categoryRepository.getCategoriesUsedByType(
                    listOf(CategoryType.EXPENSE, CategoryType.LEND, CategoryType.REPAYMENT)
                )
            }

            // For income analysis: INCOME, BORROWING, COLLECT_DEBT
            CategoryType.INCOME -> {
                categoryRepository.getCategoriesUsedByType(
                    listOf(CategoryType.INCOME, CategoryType.BORROWING, CategoryType.COLLECT_DEBT)
                )
            }

            else -> {
                categoryRepository.getCategoriesUsedByType(
                    listOf(type)
                )
            }
        }
    }
}

