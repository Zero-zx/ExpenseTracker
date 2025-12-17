package transaction.usecase

import kotlinx.coroutines.flow.first
import session.usecase.GetCurrentAccountIdUseCase
import transaction.model.Category
import transaction.model.CategoryType
import transaction.repository.CategoryRepository
import transaction.repository.TransactionRepository
import javax.inject.Inject

class GetMostUsedCategoriesUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val getCurrentAccountIdUseCase: GetCurrentAccountIdUseCase
) {
    suspend operator fun invoke(
        categoryType: CategoryType,
        limit: Int = 3
    ): List<Category> {
        val accountId = getCurrentAccountIdUseCase() ?: return emptyList()
        
        // Get category usage count
        val usageCountMap = transactionRepository.getCategoryUsageCount(accountId)
        
        if (usageCountMap.isEmpty()) {
            return emptyList()
        }
        
        // Get all categories of the specified type
        val allCategories = categoryRepository.getCategoriesByType(categoryType).first()
        
        // Create a map of category by ID for quick lookup
        val categoryMap = allCategories.associateBy { it.id }
        
        // Filter and sort by usage count - only include categories that are directly used
        return usageCountMap
            .mapNotNull { (categoryId, usageCount) ->
                categoryMap[categoryId]?.let { category ->
                    // Only include categories of the specified type
                    if (category.type == categoryType) {
                        CategoryWithUsage(category, usageCount)
                    } else {
                        null
                    }
                }
            }
            .sortedByDescending { it.usageCount }
            .take(limit)
            .map { it.category }
    }
    
    private data class CategoryWithUsage(
        val category: Category,
        val usageCount: Int
    )
}

