package presentation

/**
 * Data class representing monthly income and expense for chart display
 */
data class MonthlyIncomeExpense(
    val monthLabel: String,
    val income: Double,
    val expense: Double
)

/**
 * Data class for chart data containing list of monthly income/expense
 */
data class IncomeExpenseChartData(
    val monthlyData: List<MonthlyIncomeExpense>
)

