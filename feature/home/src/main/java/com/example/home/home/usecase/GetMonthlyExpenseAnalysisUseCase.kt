package com.example.home.home.usecase

import category.model.CategoryType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import transaction.repository.TransactionRepository
import java.util.Calendar
import javax.inject.Inject

/**
 * Use case to get monthly expense data for a date range
 */
class GetMonthlyExpenseAnalysisUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(
        accountId: Long,
        startDate: Long,
        endDate: Long
    ): Flow<List<MonthlyExpenseData>> {
        return transactionRepository.getTransactionsByDateRange(startDate, endDate)
            .map { transactions ->
                // Filter only expense transactions
                val expenseTransactions = transactions.filter {
                    it.category.type == CategoryType.EXPENSE
                }

                // Group by month
                val monthExpenseMap = mutableMapOf<String, Double>()
                val calendar = Calendar.getInstance()

                expenseTransactions.forEach { transaction ->
                    calendar.timeInMillis = transaction.createAt
                    val month = calendar.get(Calendar.MONTH) + 1 // 1-based month
                    val year = calendar.get(Calendar.YEAR)
                    val key = "$month/$year"

                    monthExpenseMap[key] = (monthExpenseMap[key] ?: 0.0) + transaction.amount
                }

                // Create list of monthly data for all months in range
                val result = mutableListOf<MonthlyExpenseData>()
                val startCalendar = Calendar.getInstance().apply { timeInMillis = startDate }
                val endCalendar = Calendar.getInstance().apply { timeInMillis = endDate }

                while (startCalendar.timeInMillis <= endCalendar.timeInMillis) {
                    val month = startCalendar.get(Calendar.MONTH) + 1
                    val year = startCalendar.get(Calendar.YEAR)
                    val key = "$month/$year"

                    result.add(
                        MonthlyExpenseData(
                            month = month,
                            year = year,
                            amount = monthExpenseMap[key] ?: 0.0
                        )
                    )

                    startCalendar.add(Calendar.MONTH, 1)
                }

                result
            }
    }
}

data class MonthlyExpenseData(
    val month: Int,
    val year: Int,
    val amount: Double
) {
    fun getMonthLabel(): String {
        return "$month"
    }

    fun getFullLabel(): String {
        return "${getMonthName(month)} $year"
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> ""
        }
    }
}

