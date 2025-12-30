package presentation.datasetting

import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar
import javax.inject.Inject

enum class DataSettingTab {
    DATE, WEEK, MONTH, QUARTER, CUSTOM
}

sealed class DataSettingOption {
    data class DateOption(val type: DateOptionType) : DataSettingOption()
    data class WeekOption(val type: WeekOptionType) : DataSettingOption()
    data class MonthOption(val type: MonthOptionType) : DataSettingOption()
    data class QuarterOption(val quarter: Int) : DataSettingOption()
    data class CustomOption(val type: CustomOptionType) : DataSettingOption()
}

enum class DateOptionType {
    TODAY, YESTERDAY, SELECT_DAY
}

enum class WeekOptionType {
    THIS_WEEK, LAST_WEEK
}

enum class MonthOptionType {
    THIS_MONTH, LAST_MONTH, SELECT_MONTH
}

enum class CustomOptionType {
    ALL_TIME, CUSTOM
}

data class DataSettingUiState(
    val selectedTab: DataSettingTab = DataSettingTab.QUARTER,
    val selectedOption: DataSettingOption? = null,
    val customFromDate: Long? = null,
    val customToDate: Long? = null
)

@HiltViewModel
class DataSettingViewModel @Inject constructor() : BaseViewModel<DataSettingUiState>() {

    private val _uiState = MutableStateFlow(DataSettingUiState())
//    val uiState: StateFlow<DataSettingUiState> = _uiState.asStateFlow()

    init {
        // Default to Quarter IV
        selectQuarter(4)
        setSuccess(_uiState.value)
    }

    fun selectTab(tab: DataSettingTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        setSuccess(_uiState.value)
    }

    fun selectDateOption(type: DateOptionType) {
        _uiState.value = _uiState.value.copy(
            selectedOption = DataSettingOption.DateOption(type)
        )
        setSuccess(_uiState.value)
    }

    fun selectWeekOption(type: WeekOptionType) {
        _uiState.value = _uiState.value.copy(
            selectedOption = DataSettingOption.WeekOption(type)
        )
        setSuccess(_uiState.value)
    }

    fun selectMonthOption(type: MonthOptionType) {
        _uiState.value = _uiState.value.copy(
            selectedOption = DataSettingOption.MonthOption(type)
        )
        setSuccess(_uiState.value)
    }

    fun selectQuarter(quarter: Int) {
        _uiState.value = _uiState.value.copy(
            selectedOption = DataSettingOption.QuarterOption(quarter)
        )
        setSuccess(_uiState.value)
    }

    fun selectCustomOption(type: CustomOptionType) {
        _uiState.value = _uiState.value.copy(
            selectedOption = DataSettingOption.CustomOption(type)
        )
        setSuccess(_uiState.value)
    }

    fun setCustomFromDate(dateMillis: Long) {
        _uiState.value = _uiState.value.copy(customFromDate = dateMillis)
        setSuccess(_uiState.value)
    }

    fun setCustomToDate(dateMillis: Long) {
        _uiState.value = _uiState.value.copy(customToDate = dateMillis)
        setSuccess(_uiState.value)
    }

    fun getDateRangeForOption(option: DataSettingOption): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        return when (option) {
            is DataSettingOption.DateOption -> {
                when (option.type) {
                    DateOptionType.TODAY -> {
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        Pair(calendar.timeInMillis, endDate)
                    }

                    DateOptionType.YESTERDAY -> {
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val start = calendar.timeInMillis
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        Pair(start, calendar.timeInMillis)
                    }

                    DateOptionType.SELECT_DAY -> {
                        // This will be handled by date picker
                        Pair(0L, endDate)
                    }
                }
            }

            is DataSettingOption.WeekOption -> {
                when (option.type) {
                    WeekOptionType.THIS_WEEK -> {
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        Pair(calendar.timeInMillis, endDate)
                    }

                    WeekOptionType.LAST_WEEK -> {
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                        calendar.add(Calendar.DAY_OF_YEAR, -7)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val start = calendar.timeInMillis
                        calendar.add(Calendar.DAY_OF_YEAR, 6)
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        Pair(start, calendar.timeInMillis)
                    }
                }
            }

            is DataSettingOption.MonthOption -> {
                when (option.type) {
                    MonthOptionType.THIS_MONTH -> {
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        Pair(calendar.timeInMillis, endDate)
                    }

                    MonthOptionType.LAST_MONTH -> {
                        calendar.add(Calendar.MONTH, -1)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        val start = calendar.timeInMillis
                        calendar.add(Calendar.MONTH, 1)
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        Pair(start, calendar.timeInMillis)
                    }

                    MonthOptionType.SELECT_MONTH -> {
                        // This will be handled by month picker
                        Pair(0L, endDate)
                    }
                }
            }

            is DataSettingOption.QuarterOption -> {
                calendar.get(Calendar.MONTH)
                val currentYear = calendar.get(Calendar.YEAR)
                val quarterStartMonth = (option.quarter - 1) * 3
                calendar.set(Calendar.YEAR, currentYear)
                calendar.set(Calendar.MONTH, quarterStartMonth)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.add(Calendar.MONTH, 3)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                Pair(start, calendar.timeInMillis)
            }

            is DataSettingOption.CustomOption -> {
                when (option.type) {
                    CustomOptionType.ALL_TIME -> {
                        Pair(0L, endDate)
                    }

                    CustomOptionType.CUSTOM -> {
                        val from = _uiState.value.customFromDate ?: 0L
                        val to = _uiState.value.customToDate ?: endDate
                        Pair(from, to)
                    }
                }
            }
        }
    }

    fun getSelectedPeriodLabel(): String {
        val option = _uiState.value.selectedOption ?: return "Quarter IV"
        return when (option) {
            is DataSettingOption.DateOption -> {
                when (option.type) {
                    DateOptionType.TODAY -> "Today"
                    DateOptionType.YESTERDAY -> "Yesterday"
                    DateOptionType.SELECT_DAY -> "Selected Day"
                }
            }

            is DataSettingOption.WeekOption -> {
                when (option.type) {
                    WeekOptionType.THIS_WEEK -> "This Week"
                    WeekOptionType.LAST_WEEK -> "Last Week"
                }
            }

            is DataSettingOption.MonthOption -> {
                when (option.type) {
                    MonthOptionType.THIS_MONTH -> "This Month"
                    MonthOptionType.LAST_MONTH -> "Last Month"
                    MonthOptionType.SELECT_MONTH -> "Selected Month"
                }
            }

            is DataSettingOption.QuarterOption -> "Quarter ${option.quarter}"
            is DataSettingOption.CustomOption -> {
                when (option.type) {
                    CustomOptionType.ALL_TIME -> "All Time"
                    CustomOptionType.CUSTOM -> {
                        val fromDate = _uiState.value.customFromDate
                        val toDate = _uiState.value.customToDate
                        if (fromDate != null && toDate != null) {
                            val dateFormat = java.text.SimpleDateFormat(
                                "dd/MM/yyyy",
                                java.util.Locale.getDefault()
                            )
                            "${dateFormat.format(java.util.Date(fromDate))} - ${
                                dateFormat.format(
                                    java.util.Date(toDate)
                                )
                            }"
                        } else {
                            "Custom Range"
                        }
                    }
                }
            }
        }
    }
}


