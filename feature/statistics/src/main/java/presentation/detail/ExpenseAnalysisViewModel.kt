package presentation.detail

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
        // Adjust date range based on tab type
        val calendar = Calendar.getInstance()
        currentEndDate = calendar.timeInMillis

        when (tabType) {
            TabType.NOW -> {
                // Current month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                currentStartDate = calendar.timeInMillis
            }

            TabType.MONTHLY -> {
                // Last 12 months
                calendar.add(Calendar.MONTH, -12)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                currentStartDate = calendar.timeInMillis
            }

            TabType.QUARTER -> {
                // Last 4 quarters (12 months)
                calendar.add(Calendar.MONTH, -12)
                currentStartDate = calendar.timeInMillis
            }

            TabType.YEAR -> {
                // Last 5 years
                calendar.add(Calendar.YEAR, -5)
                calendar.set(Calendar.DAY_OF_YEAR, 1)
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
            // Filter by selected categories:
            // - null: show all (default state)
            // - emptyList(): show nothing (deselect all)
            // - non-empty: filter by selected
            // Note: When parent category is selected, both parent ID and child IDs are in selectedCategoryIds
            // So we check if transaction.category.id is in the list (which includes child IDs)
            val matchesCategory = when {
                categoryIds == null -> true // Default: show all
                categoryIds.isEmpty() -> false // Deselect all: show nothing
                else -> {
                    // Check if transaction's category ID is in selected list
                    // Note: When parent category is selected, both parent ID and child IDs are already in selectedCategoryIds
                    // So we only need to check if transaction.category.id is in the list
                    categoryIds.contains(transaction.category.id)
                }
            }

            // Filter by selected accounts:
            // - null: show all (default state)
            // - emptyList(): show nothing (deselect all)
            // - non-empty: filter by selected
            val matchesAccount = when {
                accountIds == null -> true // Default: show all
                accountIds.isEmpty() -> false // Deselect all: show nothing
                else -> accountIds.contains(transaction.account.id) // Filter by selected
            }

            matchesCategory && matchesAccount
        }
    }

    private fun processTransactions(transactions: List<Transaction>): AnalysisData {
        val calendar = Calendar.getInstance()
        val monthlyDataMap = mutableMapOf<String, Double>()

        // Initialize 12 months with 0 values
        val startCalendar = Calendar.getInstance().apply { timeInMillis = currentStartDate }
        val endCalendar = Calendar.getInstance().apply { timeInMillis = currentEndDate }

        val currentMonth = Calendar.getInstance().apply { timeInMillis = currentStartDate }
        while (currentMonth.before(endCalendar) || currentMonth.get(Calendar.MONTH) == endCalendar.get(
                Calendar.MONTH
            )
        ) {
            val monthKey =
                "${currentMonth.get(Calendar.MONTH) + 1}/${currentMonth.get(Calendar.YEAR)}"
            monthlyDataMap[monthKey] = 0.0
            currentMonth.add(Calendar.MONTH, 1)
        }

        // Process transactions
        transactions.forEach { transaction ->
            calendar.timeInMillis = transaction.createAt
            val monthKey = "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

            monthlyDataMap[monthKey] = (monthlyDataMap[monthKey] ?: 0.0) + transaction.amount
        }

        // Convert to list sorted by month (oldest first)
        val monthlyData = monthlyDataMap.entries
            .sortedBy { it.key }
            .map { (monthKey, amount) ->
                MonthlyAnalysisItem(
                    monthLabel = monthKey,
                    amount = amount
                )
            }
            .filter { it.amount > 0 } // Only show months with expenses

        val totalAmount = transactions.sumOf { it.amount }
        val monthCount = monthlyDataMap.values.count { it > 0 }
        val averagePerMonth = if (monthCount > 0) totalAmount / monthCount else 0.0

        return AnalysisData(
            totalAmount = totalAmount,
            averagePerMonth = averagePerMonth,
            monthlyData = monthlyData
        )
    }

    fun getSelectedCategoryIds(): List<Long>? = selectedCategoryIds
    fun getSelectedAccountIds(): List<Long>? = selectedAccountIds

    fun navigateToSelectCategory() {
        // If selectedCategoryIds is null (default), pass special marker - màn B will understand as "select all"
        // If selectedCategoryIds is emptyList() (deselect all), pass emptyArray() - màn B will understand as "deselect all" 
        // If selectedCategoryIds has values, pass the array
        val categoryIds = selectedCategoryIds
        val idsToPass: Array<Long> = when {
            categoryIds == null -> arrayOf(-1L) // Special marker for "default/select all"
            categoryIds.isEmpty() -> emptyArray() // Empty = deselect all
            else -> categoryIds.toTypedArray() // Has IDs - convert List<Long> to Array<Long>
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

