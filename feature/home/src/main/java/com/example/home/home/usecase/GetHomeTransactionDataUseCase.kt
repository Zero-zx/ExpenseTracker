package com.example.home.home.usecase

import category.model.Category
import category.model.CategoryType
import com.example.home.home.model.HomeReportData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case to get processed transaction data for home screen
 */
class GetHomeReportDataUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(
        accountId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<HomeReportData> {
        return transactionRepository.getTransactionsByDateRange(accountId, startDate, endDate)
            .map { transactions ->
                processTransactions(transactions)
            }
    }

    private fun processTransactions(transactions: List<Transaction>): HomeReportData {
        var totalIncome = 0.0
        var totalExpense = 0.0

        // Map to track category expenses
        val categoryExpenseMap = mutableMapOf<Long, CategoryExpenseData>()

        transactions.forEach { transaction ->
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> {
                    totalIncome += transaction.amount
                }

                CategoryType.EXPENSE, CategoryType.BORROWING -> {
                    totalExpense += transaction.amount
                    // Track category expenses (only for EXPENSE type)
                    if (transaction.category.type == CategoryType.EXPENSE) {
                        val categoryId = transaction.category.id
                        val current = categoryExpenseMap[categoryId]
                        if (current != null) {
                            categoryExpenseMap[categoryId] = current.copy(
                                amount = current.amount + transaction.amount
                            )
                        } else {
                            categoryExpenseMap[categoryId] = CategoryExpenseData(
                                category = transaction.category,
                                amount = transaction.amount
                            )
                        }
                    }
                }

                else -> {
                    // TRANSFER, ADJUSTMENT - ignore for income/expense calculation
                }
            }
        }

        // Get top 3 categories by expense amount
        val topCategories = categoryExpenseMap.values
            .sortedByDescending { it.amount }
            .take(3)
            .toMutableList()

        // Fill up to 3 categories if less than 3
        while (topCategories.size < 3) {
            topCategories.add(
                CategoryExpenseData(
                    category = null,
                    amount = 0.0
                )
            )
        }

        // Calculate percentages for top categories
        val totalCategoryExpense = topCategories.sumOf { it.amount }
        val topCategoriesWithPercentage = topCategories.map { categoryData ->
            val percentage = if (totalCategoryExpense > 0 && categoryData.amount > 0) {
                (categoryData.amount / totalCategoryExpense) * 100
            } else {
                0.0
            }
            categoryData.copy(percentage = percentage)
        }

        return HomeReportData(
            income = totalIncome,
            expense = totalExpense,
            difference = totalIncome - totalExpense,
            topCategories = topCategoriesWithPercentage,
            hasData = transactions.isNotEmpty() && (totalIncome > 0 || totalExpense > 0)
        )
    }
}


data class CategoryExpenseData(
    val category: Category?,
    val amount: Double,
    val percentage: Double = 0.0
)

