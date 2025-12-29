package category.usecase

import category.model.Category
import category.model.CategoryType
import kotlinx.coroutines.flow.Flow
import transaction.repository.CategoryRepository
import javax.inject.Inject

/**
 * Use case to get categories for report/analysis screens
 * - EXPENSE -> returns EXPENSE, LEND, REPAYMENT
 * - INCOME -> returns INCOME, BORROWING, COLLECT_DEBT
 */
class GetReportCategoriesByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: CategoryType): Flow<List<Category>> {
        return when (type) {
            // For expense analysis: EXPENSE, LEND, REPAYMENT
            CategoryType.EXPENSE -> {
                categoryRepository.getCategoriesByTypes(
                    listOf(CategoryType.EXPENSE, CategoryType.LEND, CategoryType.REPAYMENT)
                )
            }
            
            // For income analysis: INCOME, BORROWING, COLLECT_DEBT
            CategoryType.INCOME -> {
                categoryRepository.getCategoriesByTypes(
                    listOf(CategoryType.INCOME, CategoryType.BORROWING, CategoryType.COLLECT_DEBT)
                )
            }

            // For other types, return single type
            else -> {
                categoryRepository.getCategoriesByType(type)
            }
        }
    }
}

