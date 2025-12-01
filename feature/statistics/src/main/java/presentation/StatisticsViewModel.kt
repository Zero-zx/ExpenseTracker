package presentation

import account.model.Account
import account.usecase.GetAccountsUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import transaction.model.Transaction
import transaction.usecase.GetTransactionsByDateRangeUseCase
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class ChartData(
    val entries: List<Entry>,
    val labels: List<String>
)

private fun getDefaultStartDate(): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, -30) // Last 30 days
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getDefaultEndDate(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}

data class StatisticsUiState(
    val transactions: List<Transaction> = emptyList(),
    val chartData: ChartData? = null,
    val selectedAccount: Account? = null,
    val startDate: Long = getDefaultStartDate(),
    val endDate: Long = getDefaultEndDate(),
    val accounts: List<Account> = emptyList()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getTransactionsByDateRangeUseCase: GetTransactionsByDateRangeUseCase,
    private val getAccountsUseCase: GetAccountsUseCase
) : BaseViewModel<StatisticsUiState>() {

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(StatisticsUiState())
    val statisticsState = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAccountsUseCase()
                .catch { exception ->
                    setError(exception.message ?: "Failed to load accounts")
                }
                .collect { accounts ->
                    if (accounts.isNotEmpty()) {
                        val updatedState = _uiState.value.copy(
                            accounts = accounts,
                            selectedAccount = accounts.firstOrNull()
                        )
                        _uiState.value = updatedState
                        loadTransactions()
                    }
                }
        }
    }

    fun selectAccount(account: Account) {
        _uiState.value = _uiState.value.copy(selectedAccount = account)
        loadTransactions()
    }

    fun setDateRange(startDate: Long, endDate: Long) {
        _uiState.value = _uiState.value.copy(
            startDate = startDate,
            endDate = endDate
        )
        loadTransactions()
    }

    private fun loadTransactions() {
        val state = _uiState.value
        val accountId = state.selectedAccount?.id ?: return

        viewModelScope.launch {
            getTransactionsByDateRangeUseCase(
                accountId = accountId,
                startDate = state.startDate,
                endDate = state.endDate
            )
                .onStart { setLoading() }
                .catch { exception ->
                    setError(exception.message ?: "Failed to load transactions")
                }
                .collect { transactions ->
                    val chartData = processTransactionsForChart(transactions, state.startDate, state.endDate)
                    _uiState.value = state.copy(
                        transactions = transactions,
                        chartData = chartData
                    )
                    setSuccess(_uiState.value)
                }
        }
    }

    private fun processTransactionsForChart(
        transactions: List<Transaction>,
        startDate: Long,
        endDate: Long
    ): ChartData {
        if (transactions.isEmpty()) {
            return ChartData(emptyList(), emptyList())
        }

        // Group transactions by date (day)
        val calendar = Calendar.getInstance()
        val dateMap = mutableMapOf<Long, Double>()

        transactions.forEach { transaction ->
            calendar.timeInMillis = transaction.createAt
            // Reset to start of day
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dayStart = calendar.timeInMillis

            dateMap[dayStart] = (dateMap[dayStart] ?: 0.0) + transaction.amount
        }

        // Create entries for each day in the range
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()
        
        calendar.timeInMillis = startDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        var index = 0f
        val endCalendar = Calendar.getInstance().apply {
            timeInMillis = endDate
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        while (calendar.timeInMillis <= endCalendar.timeInMillis) {
            val dayStart = calendar.timeInMillis
            val amount = dateMap[dayStart] ?: 0.0
            
            entries.add(Entry(index, amount.toFloat()))
            
            // Format date label (e.g., "Jan 15")
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            labels.add("$month/$day")
            
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            index++
        }

        return ChartData(entries, labels)
    }
}

