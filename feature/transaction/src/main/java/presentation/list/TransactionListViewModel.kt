package presentation.list

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.CategoryType
import transaction.model.Transaction
import transaction.usecase.DeleteTransactionUseCase
import transaction.usecase.GetTransactionsByDateRangeUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class TransactionListData(
    val items: List<TransactionListItem>,
    val totalIncome: Double,
    val totalExpense: Double,
    val selectedPeriod: String
)

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : BaseViewModel<TransactionListData>() {
    private val currentAccountId = 1L
    private var currentStartDate: Long = 0L
    private var currentEndDate: Long = System.currentTimeMillis()
    private var currentPeriod: String = "Quarter IV"

    // Selection mode state
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _selectedTransactions = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTransactions: StateFlow<Set<Long>> = _selectedTransactions.asStateFlow()

    init {
        loadTransactionsForQuarter(4)
    }

    fun loadTransactionsForQuarter(quarter: Int) {
        currentPeriod = "Quarter $quarter"
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)

        // Calculate quarter start and end
        val quarterStartMonth = (quarter - 1) * 3
        calendar.set(Calendar.YEAR, currentYear)
        calendar.set(Calendar.MONTH, quarterStartMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        currentStartDate = calendar.timeInMillis

        // Quarter end
        calendar.add(Calendar.MONTH, 3)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        currentEndDate = calendar.timeInMillis

        loadTransactions()
    }

    fun loadTransactionsForDateRange(startDate: Long, endDate: Long, periodLabel: String) {
        currentStartDate = startDate
        currentEndDate = endDate
        currentPeriod = periodLabel
        loadTransactions()
    }

    private fun loadTransactions() {
        setLoading()
        viewModelScope.launch {
            getTransactionsByDateRangeUseCase(currentAccountId, currentStartDate, currentEndDate)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { transactions ->
                    val groupedItems = groupTransactionsByDate(transactions)
                    val (totalIncome, totalExpense) = calculateTotals(transactions)
                    setSuccess(
                        TransactionListData(
                            items = groupedItems,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense,
                            selectedPeriod = currentPeriod
                        )
                    )
                }
        }
    }

    private fun groupTransactionsByDate(transactions: List<Transaction>): List<TransactionListItem> {
        val grouped = transactions.groupBy { transaction ->
            val dateKey = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date(transaction.createAt))
            dateKey
        }

        val items = mutableListOf<TransactionListItem>()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dayNameFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val today = Calendar.getInstance()
        val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.time)

        grouped.toSortedMap(compareByDescending { it }).forEach { (dateKey, dayTransactions) ->
            val transactionDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateKey)!!

            val dateStr = dateFormat.format(transactionDate)
            val dayName = when {
                dateKey == todayKey -> "Today"
                dateKey == getYesterdayKey() -> "Yesterday"
                else -> dayNameFormat.format(transactionDate)
            }

            // Calculate total for the day
            val dayTotal = dayTransactions.sumOf { transaction ->
                when (transaction.category.type) {
                    CategoryType.EXPENSE, CategoryType.BORROWING -> -transaction.amount
                    CategoryType.INCOME, CategoryType.LEND -> transaction.amount
                    else -> 0.0
                }
            }

            // Instead of adding a DateHeader + TransactionItem entries, add a single DateHeader with the list
            val sortedTransactions = dayTransactions.sortedByDescending { it.createAt }
            items.add(
                TransactionListItem.DateHeader(
                    dateStr,
                    dayName,
                    dayTotal,
                    sortedTransactions
                )
            )
        }

        return items
    }

    private fun getYesterdayKey(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    private fun calculateTotals(transactions: List<Transaction>): Pair<Double, Double> {
        var totalIncome = 0.0
        var totalExpense = 0.0

        transactions.forEach { transaction ->
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> {
                    totalIncome += transaction.amount
                }

                CategoryType.EXPENSE, CategoryType.BORROWING -> {
                    totalExpense += transaction.amount
                }

                else -> {}
            }
        }

        return Pair(totalIncome, totalExpense)
    }

    fun refresh() {
        loadTransactions()
    }

    // Selection mode functions
    fun enterSelectionMode() {
        _isSelectionMode.value = true
        _selectedTransactions.value = emptySet()
    }

    fun exitSelectionMode() {
        _isSelectionMode.value = false
        _selectedTransactions.value = emptySet()
    }

    fun toggleTransactionSelection(transactionId: Long) {
        val currentSelected = _selectedTransactions.value.toMutableSet()
        if (currentSelected.contains(transactionId)) {
            currentSelected.remove(transactionId)
        } else {
            currentSelected.add(transactionId)
        }
        _selectedTransactions.value = currentSelected
    }

    fun selectAllTransactions() {
        val allTransactionIds = getAllTransactionIds()
        _selectedTransactions.value = allTransactionIds.toSet()
    }

    fun clearSelection() {
        _selectedTransactions.value = emptySet()
    }

    private fun getAllTransactionIds(): List<Long> {
        val currentData = uiState.value
        if (currentData is base.UIState.Success) {
            return currentData.data.items.flatMap { item ->
                if (item is TransactionListItem.DateHeader) {
                    item.transactions.map { it.id }
                } else {
                    emptyList()
                }
            }
        }
        return emptyList()
    }

    fun deleteSelectedTransactions() {
        val selectedIds = _selectedTransactions.value
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            val currentData = uiState.value
            if (currentData is base.UIState.Success) {
                val transactionsToDelete = currentData.data.items.flatMap { item ->
                    if (item is TransactionListItem.DateHeader) {
                        item.transactions.filter { selectedIds.contains(it.id) }
                    } else {
                        emptyList()
                    }
                }

                val result = deleteTransactionUseCase.deleteTransactions(transactionsToDelete)
                if (result.isSuccess) {
                    exitSelectionMode()
                    loadTransactions() // Reload to refresh the list
                } else {
                    setError(result.exceptionOrNull()?.message ?: "Failed to delete transactions")
                }
            }
        }
    }

    fun navigateToEditTransaction(transactionId: Long) {
        navigator.navigateToEditTransaction(transactionId)
    }

    fun navigateToDataSetting() {
        navigator.navigateToDataSetting()
    }
}
