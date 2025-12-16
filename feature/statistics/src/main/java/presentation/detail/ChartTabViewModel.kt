package presentation.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.detail.model.ChartDataWithReportItems
import presentation.detail.model.ReportItem
import presentation.detail.model.TabType
import session.usecase.GetCurrentAccountIdUseCase
import transaction.model.CategoryType
import transaction.model.Transaction
import transaction.usecase.GetTransactionsByDateRangeUseCase
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ChartTabViewModel @Inject constructor(
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
    private val getCurrentAccountIdUseCase: GetCurrentAccountIdUseCase
) : BaseViewModel<ChartDataWithReportItems>() {

    private var tabType: TabType = TabType.MONTHLY

    fun loadData(tabType: TabType) {
        this.tabType = tabType
        setLoading()

        val accountId = getCurrentAccountIdUseCase() ?: run {
            setError("No account selected. Please select an account.")
            return
        }

        val (startDate, endDate) = getDateRangeForTabType(tabType)

        getTransactionsByDateRangeUseCase(accountId, startDate, endDate)
            .onEach { transactions ->
                val chartData = processTransactions(transactions, tabType)
                setSuccess(chartData)
            }
            .catch { exception ->
                Log.e("ChartTabViewModel", "Error loading transactions", exception)
                setError(exception.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    private fun getDateRangeForTabType(tabType: TabType): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        when (tabType) {
            TabType.MONTHLY -> {
                calendar.add(Calendar.MONTH, -11) // Last 12 months
            }

            TabType.QUARTER -> {
                calendar.add(Calendar.MONTH, -11) // Last 4 quarters (12 months)
            }

            TabType.YEAR -> {
                calendar.add(Calendar.YEAR, -4) // Last 5 years
            }

            TabType.CUSTOM -> {
                // For custom, we'll need to get from arguments or shared preferences
                // For now, use last 12 months as default
                calendar.add(Calendar.MONTH, -11)
            }

            else -> {
                calendar.add(Calendar.MONTH, -11)
            }
        }

        val startDate = calendar.timeInMillis
        return Pair(startDate, endDate)
    }

    private fun processTransactions(
        transactions: List<Transaction>,
        tabType: TabType
    ): ChartDataWithReportItems {
        val groupedData = when (tabType) {
            TabType.MONTHLY -> groupByMonth(transactions)
            TabType.QUARTER -> groupByQuarter(transactions)
            TabType.YEAR -> groupByYear(transactions)
            TabType.CUSTOM -> groupByCustom(transactions)
            else -> emptyMap()
        }

        val labels = groupedData.keys.sorted().toList()
        val chartDataList = labels.map { key ->
            val (income, expense) = groupedData[key] ?: Pair(0.0, 0.0)
            ChartDataWithReportItems.IncomeExpenseData(income, expense)
        }

        val reportItems = labels.map { label ->
            val (income, expense) = groupedData[label] ?: Pair(0.0, 0.0)
            ReportItem(label, income, expense)
        }

        return ChartDataWithReportItems(
            chartData = ChartDataWithReportItems.ChartData(
                labels = labels,
                data = chartDataList
            ),
            reportItems = reportItems
        )
    }

    private fun groupByMonth(transactions: List<Transaction>): Map<String, Pair<Double, Double>> {
        val map = mutableMapOf<String, Pair<Double, Double>>()
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        transactions.forEach { transaction ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = transaction.createAt
            }
            val monthName = monthNames[cal.get(Calendar.MONTH)]
            val key = monthName

            val (currentIncome, currentExpense) = map[key] ?: Pair(0.0, 0.0)
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> {
                    map[key] = Pair(currentIncome + transaction.amount, currentExpense)
                }

                CategoryType.EXPENSE, CategoryType.BORROWING -> {
                    map[key] = Pair(currentIncome, currentExpense + transaction.amount)
                }

                else -> {}
            }
        }

        return map
    }

    private fun groupByQuarter(transactions: List<Transaction>): Map<String, Pair<Double, Double>> {
        val map = mutableMapOf<String, Pair<Double, Double>>()

        transactions.forEach { transaction ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = transaction.createAt
            }
            val month = cal.get(Calendar.MONTH)
            val quarterRoman = when (val quarter = (month / 3) + 1) {
                1 -> "I"
                2 -> "II"
                3 -> "III"
                4 -> "IV"
                else -> quarter.toString()
            }
            val key = "Quarter $quarterRoman"

            val (currentIncome, currentExpense) = map[key] ?: Pair(0.0, 0.0)
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> {
                    map[key] = Pair(currentIncome + transaction.amount, currentExpense)
                }

                CategoryType.EXPENSE, CategoryType.BORROWING -> {
                    map[key] = Pair(currentIncome, currentExpense + transaction.amount)
                }

                else -> {}
            }
        }

        return map
    }

    private fun groupByYear(transactions: List<Transaction>): Map<String, Pair<Double, Double>> {
        val map = mutableMapOf<String, Pair<Double, Double>>()

        transactions.forEach { transaction ->
            val cal = Calendar.getInstance().apply {
                timeInMillis = transaction.createAt
            }
            val year = cal.get(Calendar.YEAR)
            val key = year.toString()

            val (currentIncome, currentExpense) = map[key] ?: Pair(0.0, 0.0)
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> {
                    map[key] = Pair(currentIncome + transaction.amount, currentExpense)
                }

                CategoryType.EXPENSE, CategoryType.BORROWING -> {
                    map[key] = Pair(currentIncome, currentExpense + transaction.amount)
                }

                else -> {}
            }
        }

        return map
    }

    private fun groupByCustom(transactions: List<Transaction>): Map<String, Pair<Double, Double>> {
        // For custom, show as single item
        var income = 0.0
        var expense = 0.0

        transactions.forEach { transaction ->
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> income += transaction.amount
                CategoryType.EXPENSE, CategoryType.BORROWING -> expense += transaction.amount
                else -> {}
            }
        }

        return mapOf("Custom Range" to Pair(income, expense))
    }
}

