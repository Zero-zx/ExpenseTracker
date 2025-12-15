package presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import transaction.model.CategoryType
import transaction.model.Transaction
import transaction.usecase.GetTransactionsByDateRangeUseCase
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
    private val navigator: navigation.Navigator
) : BaseViewModel<IncomeExpenseChartData>() {

    companion object {
        private const val ACCOUNT_ID = 1L
        private const val MONTHS_TO_SHOW = 5
    }

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        setLoading()

        // Calculate date range for last 5 months
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, -MONTHS_TO_SHOW)
        val startDate = calendar.timeInMillis

        getTransactionsByDateRangeUseCase(ACCOUNT_ID, startDate, endDate)
            .onEach { transactions ->
                val chartData = processTransactions(transactions)
                setSuccess(chartData)
            }
            .catch { exception ->
                Log.e("ReportsViewModel", "Error loading transactions", exception)
                setError(exception.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    private fun processTransactions(transactions: List<Transaction>): IncomeExpenseChartData {
        val calendar = Calendar.getInstance()
        val monthlyDataMap = mutableMapOf<String, Pair<Double, Double>>()

        // Initialize last 5 months with 0 values
        for (i in 0 until MONTHS_TO_SHOW) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MONTH, -i)
            val monthKey = getMonthKey(calendar)
            monthlyDataMap[monthKey] = Pair(0.0, 0.0)
        }

        // Variables to track total income and expense
        var totalIncome = 0.0
        var totalExpense = 0.0

        // Process transactions
        transactions.forEach { transaction ->
            val transactionCalendar = Calendar.getInstance().apply {
                timeInMillis = transaction.createAt
            }
            val monthKey = getMonthKey(transactionCalendar)

            // Calculate totals regardless of month (for all transactions in range)
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> {
                    totalIncome += transaction.amount
                }
                CategoryType.EXPENSE, CategoryType.BORROWING -> {
                    totalExpense += transaction.amount
                }
                else -> {}
            }

            // Only process transactions from last 5 months for chart
            if (monthlyDataMap.containsKey(monthKey)) {
                val (currentIncome, currentExpense) = monthlyDataMap[monthKey] ?: Pair(0.0, 0.0)

                when (transaction.category.type) {
                    CategoryType.INCOME, CategoryType.LEND -> {
                        // Income: IN and LEND
                        monthlyDataMap[monthKey] = Pair(
                            currentIncome + transaction.amount,
                            currentExpense
                        )
                    }

                    CategoryType.EXPENSE, CategoryType.BORROWING -> {
                        // Expense: OUT and LOAN
                        monthlyDataMap[monthKey] = Pair(
                            currentIncome,
                            currentExpense + transaction.amount
                        )
                    }

                    else -> {}
                }
            }
        }

        // Convert to list sorted by month (oldest first)
        val monthlyData = monthlyDataMap.entries
            .sortedByDescending { it.key } // Sort descending (newest first)
            .reversed() // Reverse to get oldest first
            .map { (monthKey, values) ->
                MonthlyIncomeExpense(
                    monthLabel = getMonthLabel(monthKey),
                    income = values.first,
                    expense = values.second
                )
            }

        return IncomeExpenseChartData(
            monthlyData = monthlyData,
            totalIncome = totalIncome,
            totalExpense = totalExpense
        )
    }

    private fun getMonthKey(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return "$year-${String.format("%02d", month + 1)}"
    }

    private fun getMonthLabel(monthKey: String): String {
        val parts = monthKey.split("-")
        if (parts.size == 2) {
            val month = parts[1].toIntOrNull() ?: 0
            if (month in 1..12) {
                return month.toString()
            }
        }
        return monthKey
    }

    fun navigateToIncomeExpenseDetail() {
        navigator.navigateToIncomeExpenseDetail()
    }
}

