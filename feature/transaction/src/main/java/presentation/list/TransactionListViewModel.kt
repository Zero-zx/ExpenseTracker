package presentation.list

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import transaction.model.CategoryType
import transaction.model.Transaction
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
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase
) : BaseViewModel<TransactionListData>() {
    private val currentAccountId = 1L
    private var currentStartDate: Long = 0L
    private var currentEndDate: Long = System.currentTimeMillis()
    private var currentPeriod: String = "Quarter IV"

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
}
