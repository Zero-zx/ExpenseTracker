package com.example.home.home

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import com.example.home.home.model.HomeReportData
import com.example.home.home.usecase.GetHomeReportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import navigation.Navigator
import session.usecase.GetCurrentAccountIdUseCase
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getHomeTransactionDataUseCase: GetHomeReportDataUseCase,
    private val getCurrentAccountIdUseCase: GetCurrentAccountIdUseCase
) : BaseViewModel<HomeReportData>() {

    private var currentTimePeriod: TimePeriod = TimePeriod.THIS_MONTH

    init {
        loadTransactionData()
    }

    fun loadTransactionData(timePeriod: TimePeriod = currentTimePeriod) {
        currentTimePeriod = timePeriod
        setLoading()

        val accountId = getCurrentAccountIdUseCase() ?: run {
            setError("No account selected. Please select an account.")
            return
        }

        val (startDate, endDate) = getDateRangeForPeriod(timePeriod)

        getHomeTransactionDataUseCase(accountId, startDate, endDate)
            .onEach { data ->
                setSuccess(data)
            }
            .catch { exception ->
                setError(exception.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    private fun getDateRangeForPeriod(period: TimePeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        when (period) {
            TimePeriod.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            TimePeriod.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            TimePeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            TimePeriod.QUARTER_PRESENT -> {
                val currentMonth = calendar.get(Calendar.MONTH)
                val quarterStartMonth = (currentMonth / 3) * 3
                calendar.set(Calendar.MONTH, quarterStartMonth)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            TimePeriod.THIS_YEAR -> {
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }

        val startDate = calendar.timeInMillis
        return Pair(startDate, endDate)
    }

    fun navigateToEventList() {
        navigator.navigateToEventList()
    }

    fun navigateToTransaction() {
        navigator.navigateToTransaction()
    }

    fun navigateToReportDetailContainer() {
        navigator.navigateToReportDetailContainer()
    }
}

enum class TimePeriod(val displayName: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    QUARTER_PRESENT("Quarter Present"),
    THIS_YEAR("This Year")
}