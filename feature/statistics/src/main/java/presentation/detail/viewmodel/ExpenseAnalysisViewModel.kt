package presentation.detail.viewmodel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import category.model.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import navigation.Navigator
import presentation.detail.model.AnalysisData
import presentation.detail.model.MonthlyAnalysisItem
import presentation.detail.model.TabType
import session.usecase.GetCurrentAccountIdUseCase
import transaction.model.Transaction
import transaction.usecase.GetTransactionsByDateRangeUseCase
import transaction.usecase.GetTransactionsByTypeDateRangeUseCase
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpenseAnalysisViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
    private val getTransactionsByTypeDateRangeUseCase: GetTransactionsByTypeDateRangeUseCase,
    private val getCurrentAccountIdUseCase: GetCurrentAccountIdUseCase
) : BaseViewModel<AnalysisData>() {


    private var currentStartDate: Long = 0L
    private var currentEndDate: Long = System.currentTimeMillis()
    private var selectedCategoryIds: List<Long>? = null
    private var selectedAccountIds: List<Long>? = null
    private var currentTabType: TabType = TabType.MONTHLY

    init {
        // Default: Last 12 months
        val calendar = Calendar.getInstance()
        currentEndDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, -12)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        currentStartDate = calendar.timeInMillis

        loadExpenseAnalysis()
    }

    fun loadData(tabType: TabType) {
        currentTabType = tabType
        // Adjust date range based on tab type
        val calendar = Calendar.getInstance()
        currentEndDate = calendar.timeInMillis

        when (tabType) {
            TabType.NOW -> {
                // Current month - all days
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                currentStartDate = calendar.timeInMillis
            }

            TabType.MONTHLY -> {
                // 12 months of current year
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                currentStartDate = calendar.timeInMillis
            }

            TabType.QUARTER -> {
                // Last 4 quarters (12 months)
                calendar.add(Calendar.MONTH, -12)
                currentStartDate = calendar.timeInMillis
            }

            TabType.YEAR -> {
                // Last 5 years
                calendar.add(Calendar.YEAR, -4) // -4 because we want 5 years total (current + 4 past)
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                currentStartDate = calendar.timeInMillis
            }

            TabType.CUSTOM -> {
                // Use current date range (already set)
            }
        }

        loadExpenseAnalysis()
    }

    fun loadExpenseAnalysis(
        startDate: Long? = null,
        endDate: Long? = null,
        categoryIds: List<Long>? = null,
        accountIds: List<Long>? = null
    ) {
        startDate?.let { currentStartDate = it }
        endDate?.let { currentEndDate = it }
        // Always update selectedCategoryIds, even if categoryIds is null (means show all)
        selectedCategoryIds = categoryIds
        // Always update selectedAccountIds, even if accountIds is null (means show all)
        selectedAccountIds = accountIds

        setLoading()

        val accountId = getCurrentAccountIdUseCase() ?: run {
            setError("No account selected. Please select an account.")
            return
        }

        getTransactionsByTypeDateRangeUseCase(
            currentStartDate,
            currentEndDate,
            listOf(CategoryType.EXPENSE, CategoryType.LEND, CategoryType.REPAYMENT)
        )
            .onEach { transactions ->
                val filteredTransactions = filterTransactions(transactions)
                val analysisData = processTransactions(filteredTransactions)
                setSuccess(analysisData)
            }
            .catch { exception ->
                setError(exception.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    private fun filterTransactions(transactions: List<Transaction>): List<Transaction> {
        val categoryIds = selectedCategoryIds
        val accountIds = selectedAccountIds
        
        return transactions.filter { transaction ->
            val matchesCategory = when {
                categoryIds.isNullOrEmpty() -> true
                else -> {
                    categoryIds.contains(transaction.category.id)
                }
            }

            val matchesAccount = when {
                accountIds == null -> true
                accountIds.isEmpty() -> false
                else -> accountIds.contains(transaction.account.id)
            }

            matchesCategory && matchesAccount
        }
    }

    private fun processTransactions(transactions: List<Transaction>): AnalysisData {
        val calendar = Calendar.getInstance()
        val dataMap = mutableMapOf<String, Double>()

        when (currentTabType) {
            TabType.NOW -> {
                // Group by day of current month
                val startCalendar = Calendar.getInstance().apply { timeInMillis = currentStartDate }
                val endCalendar = Calendar.getInstance().apply { timeInMillis = currentEndDate }
                val daysInMonth = endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                // Initialize all days with 0
                for (day in 1..daysInMonth) {
                    dataMap[day.toString()] = 0.0
                }

                // Process transactions
                transactions.forEach { transaction ->
                    calendar.timeInMillis = transaction.createAt
                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                    dataMap[day.toString()] = (dataMap[day.toString()] ?: 0.0) + transaction.amount
                }
            }

            TabType.MONTHLY -> {
                // Group by 12 months of current year
                val startCalendar = Calendar.getInstance().apply { timeInMillis = currentStartDate }
                val endCalendar = Calendar.getInstance().apply { timeInMillis = currentEndDate }

                // Initialize 12 months with 0 values
                val currentMonth = Calendar.getInstance().apply { timeInMillis = currentStartDate }
                while (currentMonth.before(endCalendar) || 
                       (currentMonth.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) && 
                        currentMonth.get(Calendar.MONTH) <= endCalendar.get(Calendar.MONTH))) {
                    val monthKey = "${currentMonth.get(Calendar.MONTH) + 1}"
                    dataMap[monthKey] = 0.0
                    currentMonth.add(Calendar.MONTH, 1)
                }

                // Process transactions
                transactions.forEach { transaction ->
                    calendar.timeInMillis = transaction.createAt
                    val month = calendar.get(Calendar.MONTH) + 1
                    dataMap[month.toString()] = (dataMap[month.toString()] ?: 0.0) + transaction.amount
                }
            }

            TabType.YEAR -> {
                // Group by 5 latest years
                val endCalendar = Calendar.getInstance().apply { timeInMillis = currentEndDate }
                val startCalendar = Calendar.getInstance().apply { timeInMillis = currentStartDate }

                // Initialize 5 years with 0 values
                val currentYear = Calendar.getInstance().apply { timeInMillis = currentStartDate }
                while (currentYear.get(Calendar.YEAR) <= endCalendar.get(Calendar.YEAR)) {
                    val yearKey = currentYear.get(Calendar.YEAR).toString()
                    dataMap[yearKey] = 0.0
                    currentYear.add(Calendar.YEAR, 1)
                }

                // Process transactions
                transactions.forEach { transaction ->
                    calendar.timeInMillis = transaction.createAt
                    val year = calendar.get(Calendar.YEAR).toString()
                    dataMap[year] = (dataMap[year] ?: 0.0) + transaction.amount
                }
            }

            else -> {
                // Default: Group by month (MM/yyyy)
                val startCalendar = Calendar.getInstance().apply { timeInMillis = currentStartDate }
                val endCalendar = Calendar.getInstance().apply { timeInMillis = currentEndDate }

                val currentMonth = Calendar.getInstance().apply { timeInMillis = currentStartDate }
                while (currentMonth.before(endCalendar) || currentMonth.get(Calendar.MONTH) == endCalendar.get(
                        Calendar.MONTH
                    )
                ) {
                    val monthKey =
                        "${currentMonth.get(Calendar.MONTH) + 1}/${currentMonth.get(Calendar.YEAR)}"
                    dataMap[monthKey] = 0.0
                    currentMonth.add(Calendar.MONTH, 1)
                }

                transactions.forEach { transaction ->
                    calendar.timeInMillis = transaction.createAt
                    val monthKey = "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                    dataMap[monthKey] = (dataMap[monthKey] ?: 0.0) + transaction.amount
                }
            }
        }

        // Convert to list sorted by key (oldest first)
        val monthlyData = dataMap.entries
            .sortedBy { it.key }
            .map { (label, amount) ->
                MonthlyAnalysisItem(
                    monthLabel = label,
                    amount = amount
                )
            }

        val totalAmount = transactions.sumOf { it.amount }
        val periodCount = when (currentTabType) {
            TabType.NOW -> {
                val endCalendar = Calendar.getInstance().apply { timeInMillis = currentEndDate }
                endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH).toDouble()
            }
            TabType.MONTHLY -> 12.0
            TabType.YEAR -> 5.0
            else -> dataMap.values.count { it > 0 }.toDouble().coerceAtLeast(1.0)
        }
        val averagePerPeriod = if (periodCount > 0) totalAmount / periodCount else 0.0

        return AnalysisData(
            totalAmount = totalAmount,
            averagePerMonth = averagePerPeriod,
            monthlyData = monthlyData
        )
    }

    fun getSelectedCategoryIds(): List<Long>? = selectedCategoryIds
    fun getSelectedAccountIds(): List<Long>? = selectedAccountIds

    fun navigateToSelectCategory() {
        val categoryIds = selectedCategoryIds
        val idsToPass: Array<Long> = when {
            categoryIds == null -> arrayOf(-1L)
            categoryIds.isEmpty() -> emptyArray()
            else -> categoryIds.toTypedArray()
        }
        navigator.navigateToSelectReportCategory(
            CategoryType.EXPENSE.name,
            idsToPass
        )
    }

    fun navigateToSelectAccount() {
        navigator.navigateToSelectReportAccount()
    }
}

