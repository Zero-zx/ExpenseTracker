package presentation.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import transaction.model.CategoryType
import transaction.model.Transaction
import usecase.GetTransactionsByDateRangeUseCase
import presentation.detail.model.ReportItem
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NowTabViewModel @Inject constructor(
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase
) : BaseViewModel<List<ReportItem>>() {

    companion object {
        private const val ACCOUNT_ID = 1L
    }

    init {
        loadReportItems()
    }

    private fun loadReportItems() {
        setLoading()

        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        // Calculate date ranges
        val todayStart = getStartOfDay(calendar)
        val thisWeekStart = getStartOfWeek(calendar)
        val thisMonthStart = getStartOfMonth(calendar)
        val thisQuarterStart = getStartOfQuarter(calendar)
        val thisYearStart = getStartOfYear(calendar)

        val reportItems = mutableListOf<ReportItem>()

        // Load Today
        loadTransactionsForRange(todayStart, now) { income, expense ->
            reportItems.add(ReportItem("Today", income, expense))
            checkAndSetSuccess(reportItems)
        }

        // Load This Week
        loadTransactionsForRange(thisWeekStart, now) { income, expense ->
            reportItems.add(ReportItem("This Week", income, expense))
            checkAndSetSuccess(reportItems)
        }

        // Load This Month
        loadTransactionsForRange(thisMonthStart, now) { income, expense ->
            reportItems.add(ReportItem("This Month", income, expense))
            checkAndSetSuccess(reportItems)
        }

        // Load This Quarter
        loadTransactionsForRange(thisQuarterStart, now) { income, expense ->
            reportItems.add(ReportItem("This Quarter", income, expense))
            checkAndSetSuccess(reportItems)
        }

        // Load This Year
        loadTransactionsForRange(thisYearStart, now) { income, expense ->
            reportItems.add(ReportItem("This Year", income, expense))
            checkAndSetSuccess(reportItems)
        }
    }

    private var loadedCount = 0
    private val totalCount = 5

    private fun checkAndSetSuccess(reportItems: MutableList<ReportItem>) {
        loadedCount++
        if (loadedCount == totalCount) {
            setSuccess(reportItems)
        }
    }

    private fun loadTransactionsForRange(
        startDate: Long,
        endDate: Long,
        onResult: (Double, Double) -> Unit
    ) {
        getTransactionsByDateRangeUseCase(ACCOUNT_ID, startDate, endDate)
            .onEach { transactions ->
                val (income, expense) = calculateIncomeExpense(transactions)
                onResult(income, expense)
            }
            .catch { exception ->
                Log.e("NowTabViewModel", "Error loading transactions", exception)
                onResult(0.0, 0.0)
            }
            .launchIn(viewModelScope)
    }

    private fun calculateIncomeExpense(transactions: List<Transaction>): Pair<Double, Double> {
        var income = 0.0
        var expense = 0.0

        transactions.forEach { transaction ->
            when (transaction.category.type) {
                CategoryType.INCOME, CategoryType.LEND -> income += transaction.amount
                CategoryType.EXPENSE, CategoryType.BORROWING -> expense += transaction.amount
                else -> {}
            }
        }

        return Pair(income, expense)
    }

    private fun getStartOfDay(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getStartOfWeek(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getStartOfMonth(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getStartOfQuarter(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        val month = cal.get(Calendar.MONTH)
        val quarterStartMonth = (month / 3) * 3
        cal.set(Calendar.MONTH, quarterStartMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getStartOfYear(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

