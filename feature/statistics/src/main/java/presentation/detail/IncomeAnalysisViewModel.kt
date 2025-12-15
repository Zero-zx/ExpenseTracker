package presentation.detail

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import presentation.detail.model.AnalysisData
import presentation.detail.model.MonthlyAnalysisItem
import transaction.model.CategoryType
import transaction.model.Transaction
import transaction.usecase.GetTransactionsByDateRangeUseCase
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class IncomeAnalysisViewModel @Inject constructor(
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase
) : BaseViewModel<AnalysisData>() {

    companion object {
        private const val ACCOUNT_ID = 1L
    }

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
        
        loadIncomeAnalysis()
    }

    fun loadIncomeAnalysis(
        startDate: Long? = null,
        endDate: Long? = null,
        categoryIds: List<Long>? = null,
        accountIds: List<Long>? = null
    ) {
        startDate?.let { currentStartDate = it }
        endDate?.let { currentEndDate = it }
        selectedCategoryIds = categoryIds
        selectedAccountIds = accountIds
        
        setLoading()

        getTransactionsByDateRangeUseCase(ACCOUNT_ID, currentStartDate, currentEndDate)
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
        return transactions.filter { transaction ->
            // Filter by category type (only INCOME)
            val isIncome = transaction.category.type == CategoryType.INCOME
            
            // Filter by selected categories if any
            val matchesCategory = selectedCategoryIds?.isEmpty() != false || 
                                 selectedCategoryIds?.contains(transaction.category.id) == true
            
            // Filter by selected accounts if any
            val matchesAccount = selectedAccountIds?.isEmpty() != false ||
                                selectedAccountIds?.contains(transaction.account.id) == true
            
            isIncome && matchesCategory && matchesAccount
        }
    }

    private fun processTransactions(transactions: List<Transaction>): AnalysisData {
        val calendar = Calendar.getInstance()
        val monthlyDataMap = mutableMapOf<String, Double>()

        // Initialize 12 months with 0 values
        val startCalendar = Calendar.getInstance().apply { timeInMillis = currentStartDate }
        val endCalendar = Calendar.getInstance().apply { timeInMillis = currentEndDate }
        
        val currentMonth = Calendar.getInstance().apply { timeInMillis = currentStartDate }
        while (currentMonth.before(endCalendar) || currentMonth.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) {
            val monthKey = "${currentMonth.get(Calendar.MONTH) + 1}/${currentMonth.get(Calendar.YEAR)}"
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
            .filter { it.amount > 0 } // Only show months with income

        val totalAmount = transactions.sumOf { it.amount }
        val monthCount = monthlyDataMap.values.count { it > 0 }
        val averagePerMonth = if (monthCount > 0) totalAmount / monthCount else 0.0

        return AnalysisData(
            totalAmount = totalAmount,
            averagePerMonth = averagePerMonth,
            monthlyData = monthlyData
        )
    }
}

