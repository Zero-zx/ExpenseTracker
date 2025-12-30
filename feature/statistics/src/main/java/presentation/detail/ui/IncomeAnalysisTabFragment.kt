package presentation.detail.ui

import android.graphics.Color
import android.os.Bundle
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.statistics.R
import com.example.statistics.databinding.FragmentTabAnalysisBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import helpers.formatAsCurrency
import presentation.detail.adapter.MonthlyAnalysisAdapter
import presentation.detail.model.AnalysisData
import presentation.detail.model.MonthlyAnalysisItem
import presentation.detail.model.TabType
import presentation.detail.viewmodel.IncomeAnalysisViewModel
import presentation.detail.viewmodel.SharedViewModel
import ui.showWarningToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class IncomeAnalysisTabFragment : BaseFragment<FragmentTabAnalysisBinding>(
    FragmentTabAnalysisBinding::inflate
) {
    private val viewModel: IncomeAnalysisViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by hiltNavGraphViewModels(R.id.statistics_nav_graph)
    private val dateFormatter = SimpleDateFormat("MM/yyyy", Locale.getDefault())
    private val dateFormatterFull = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val yearFormatter = SimpleDateFormat("yyyy", Locale.getDefault())

    private val monthlyAdapter = MonthlyAnalysisAdapter(
        onItemClick = { item ->
            // TODO: Navigate to monthly detail
        },
        isExpense = false
    )

    private var tabType: TabType = TabType.MONTHLY
    private var isChartInitialized = false

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"

        fun newInstance(tabType: TabType): IncomeAnalysisTabFragment {
            return IncomeAnalysisTabFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }

    override fun initView() {
        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? TabType
        tabType = tabTypeArg ?: TabType.MONTHLY

        setupRecyclerView()
        setupFilters()
        viewModel.loadData(tabType)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewMonthlyBreakdown.apply {
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext())
            }
            adapter = monthlyAdapter
        }
    }

    private fun setupFilters() {
        // Date range filter
        binding.layoutDateRange.setOnClickListener {
            // TODO: Show date range picker
        }

        // Category filter
        binding.layoutCategoryFilter.setOnClickListener {
            viewModel.navigateToSelectCategory()
        }

        // Account filter
        binding.layoutAccountFilter.setOnClickListener {
            viewModel.navigateToSelectAccount()
        }

        updateDateRangeDisplay()
        updateCategoryDisplay()
        updateAccountDisplay()
    }

    private fun updateCategoryDisplay() {
        val selectedIds = viewModel.getSelectedCategoryIds()
        binding.textViewCategory.text = if (selectedIds.isNullOrEmpty()) {
            getString(R.string.text_all_income_categories)
        } else {
            "${selectedIds.size} categories"
        }
    }

    private fun updateAccountDisplay() {
        val selectedIds = viewModel.getSelectedAccountIds()
        binding.textViewAccount.text = if (selectedIds.isNullOrEmpty()) {
            getString(R.string.text_all_accounts)
        } else {
            "${selectedIds.size} accounts"
        }
    }

    private fun updateDateRangeDisplay() {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        val startDate: Long
        val startText: String
        val endText: String

        when (tabType) {
            TabType.NOW -> {
                // Current month: 01/12/2025 - 31/12/2025
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startDate = calendar.timeInMillis
                
                val startCal = Calendar.getInstance().apply { timeInMillis = startDate }
                val endCal = Calendar.getInstance().apply { timeInMillis = endDate }
                endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
                
                startText = dateFormatterFull.format(startCal.time)
                endText = dateFormatterFull.format(endCal.time)
            }
            TabType.MONTHLY -> {
                // 12 months of current year: 01/2025 - 12/2025
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startDate = calendar.timeInMillis
                
                val startCal = Calendar.getInstance().apply { timeInMillis = startDate }
                val endCal = Calendar.getInstance().apply { timeInMillis = endDate }
                endCal.set(Calendar.MONTH, Calendar.DECEMBER)
                
                startText = dateFormatter.format(startCal.time)
                endText = dateFormatter.format(endCal.time)
            }
            TabType.YEAR -> {
                // Years: 2020 - 2025
                calendar.set(Calendar.YEAR, 2020)
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                startDate = calendar.timeInMillis
                
                val startCal = Calendar.getInstance().apply { timeInMillis = startDate }
                val endCal = Calendar.getInstance().apply { 
                    timeInMillis = endDate
                    set(Calendar.YEAR, 2025)
                    set(Calendar.MONTH, Calendar.DECEMBER)
                    set(Calendar.DAY_OF_MONTH, 31)
                }
                
                startText = yearFormatter.format(startCal.time)
                endText = yearFormatter.format(endCal.time)
            }
            else -> {
                calendar.add(Calendar.MONTH, -12)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                startDate = calendar.timeInMillis
                
                startText = dateFormatter.format(Calendar.getInstance().apply { timeInMillis = startDate }.time)
                endText = dateFormatter.format(Calendar.getInstance().apply { timeInMillis = endDate }.time)
            }
        }

        binding.textViewDateRange.text = "$startText - $endText"
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {}
                is UIState.Loading -> {}
                is UIState.Success -> {
                    updateUI(state.data)
                }

                is UIState.Error -> {
                    showWarningToast(state.message)
                }
            }
        }

        collectState(sharedViewModel.selectedIds) { selectedIds ->
            handleCategorySelection(selectedIds)
        }
    }

    fun handleCategorySelection(categoryIds: List<Long>? = null) {
        viewModel.loadIncomeAnalysis(
            categoryIds = categoryIds,
            accountIds = viewModel.getSelectedAccountIds()
        )
        updateCategoryDisplay()
    }

    private fun updateUI(data: AnalysisData) {
        binding.apply {
            // Update summary
            textViewTotalIncome.text = data.totalAmount.formatAsCurrency()
            textViewAverageIncome.text = data.averagePerMonth.formatAsCurrency()

            // Update label text based on tab type
            labelAverageIncome.text = when (tabType) {
                TabType.NOW -> getString(R.string.text_average_collection_day)
                TabType.MONTHLY -> getString(R.string.text_average_collection_month)
                TabType.YEAR -> getString(R.string.text_average_collection_year)
                else -> getString(R.string.text_average_collection_month)
            }

            // Update chart
            updateChart(data.monthlyData)

            // Update monthly breakdown list
            monthlyAdapter.submitList(data.monthlyData)
        }
    }

    private fun updateChart(monthlyData: List<MonthlyAnalysisItem>) {
        val barChart = binding.barChart

        // Only setup config once
        if (!isChartInitialized) {
            configureChart()
            isChartInitialized = true
        }

        // Update data
        val barData = prepareBarData(monthlyData)
        barChart.data = barData
        barChart.notifyDataSetChanged()
        barChart.invalidate()
    }

    // 3. Configure chart appearance
    private fun configureChart() {
        val barChart = binding.barChart

        barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false

            // Disable all interactions to match the clean look
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)

            // Remove all extra spacing
            setDrawGridBackground(false)
            extraTopOffset = 0f
            extraBottomOffset = 0f
            extraLeftOffset = 0f
            extraRightOffset = 0f

            // Disable highlighting
            isHighlightPerTapEnabled = false
        }

        // Configure X-axis - will be updated in prepareBarData based on tabType
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(true)
            axisLineColor = Color.LTGRAY
            axisLineWidth = 1f
            textColor = Color.GRAY
            textSize = 10f
            granularity = 1f
        }

        // Configure Y-axis (left)
        barChart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = "#E5E5E5".toColorInt()
            gridLineWidth = 0.5f
            setDrawAxisLine(false)
            textColor = Color.GRAY
            textSize = 10f

            // Start from 0 with no extra space
            axisMinimum = 0f
            setDrawZeroLine(false)

            // Format values in thousands
            valueFormatter = MillionValueFormatter()

            // Remove spacing
            spaceTop = 10f // Small space at top for visibility
            spaceBottom = 0f
        }

        // Disable right Y-axis
        barChart.axisRight.isEnabled = false

        // Set viewport to start from bottom
        barChart.setViewPortOffsets(
            40f, // left padding for y-axis labels
            10f, // top padding
            10f, // right padding
            0f  // bottom padding for x-axis labels
        )
    }

    // 4. Prepare bar data
    private fun prepareBarData(monthlyData: List<MonthlyAnalysisItem>): BarData {
        val barChart = binding.barChart

        if (monthlyData.isEmpty()) {
            barChart.setNoDataText("No Data")
            barChart.setNoDataTextColor(Color.GRAY)
            return BarData()
        }

        val entries = mutableListOf<BarEntry>()
        var maxValue = 0f
        val dataMap = monthlyData.associate { it.monthLabel to it.amount }

        when (tabType) {
            TabType.NOW -> {
                // Display all days of current month
                val calendar = Calendar.getInstance()
                val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                // Configure X-axis for days
                barChart.xAxis.apply {
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val day = value.toInt() + 1
                            return if (day in 1..daysInMonth) day.toString() else ""
                        }
                    }
                    setLabelCount(daysInMonth, false)
                    axisMinimum = 0f
                    axisMaximum = (daysInMonth - 1).toFloat()
                }

                // Create entries for each day
                for (day in 1..daysInMonth) {
                    val amount = dataMap[day.toString()] ?: 0.0
                    val value = (amount / 1_000).toFloat()
                    entries.add(BarEntry((day - 1).toFloat(), value))
                    if (value > maxValue) maxValue = value
                }
            }

            TabType.MONTHLY -> {
                // Display 12 months of current year
                // Configure X-axis for months
                barChart.xAxis.apply {
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val month = value.toInt() + 1
                            return if (month in 1..12) month.toString() else ""
                        }
                    }
                    setLabelCount(12, false)
                    axisMinimum = 0f
                    axisMaximum = 11f
                }

                // Create entries for each month
                for (month in 1..12) {
                    val amount = dataMap[month.toString()] ?: 0.0
                    val value = (amount / 1_000).toFloat()
                    entries.add(BarEntry((month - 1).toFloat(), value))
                    if (value > maxValue) maxValue = value
                }
            }

            TabType.YEAR -> {
                // Display 6 years: 2020-2025
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val startYear = 2020
                val endYear = currentYear.coerceAtMost(2025)
                val yearCount = endYear - startYear + 1

                // Configure X-axis for years
                barChart.xAxis.apply {
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val yearIndex = value.toInt()
                            val year = startYear + yearIndex
                            return if (year in startYear..endYear) year.toString() else ""
                        }
                    }
                    setLabelCount(yearCount, false)
                    axisMinimum = 0f
                    axisMaximum = (yearCount - 1).toFloat()
                }

                // Create entries for each year
                for (i in 0 until yearCount) {
                    val year = (startYear + i).toString()
                    val amount = dataMap[year] ?: 0.0
                    val value = (amount / 1_000).toFloat()
                    entries.add(BarEntry(i.toFloat(), value))
                    if (value > maxValue) maxValue = value
                }
            }

            else -> {
                // Default: 12 months
                barChart.xAxis.apply {
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val month = value.toInt() + 1
                            return if (month in 1..12) month.toString() else ""
                        }
                    }
                    setLabelCount(12, false)
                    axisMinimum = 0f
                    axisMaximum = 11f
                }

                for (month in 1..12) {
                    val amount = dataMap[month.toString()] ?: 0.0
                    val value = (amount / 1_000).toFloat()
                    entries.add(BarEntry((month - 1).toFloat(), value))
                    if (value > maxValue) maxValue = value
                }
            }
        }

        // Configure Y-axis labels based on tab type
        when (tabType) {
            TabType.MONTHLY, TabType.YEAR -> {
                // For Month and Year tabs: set fixed label count (5 labels: 0, 20, 40, 60, 80...)
                // Calculate rounded max value to nearest 20
                val targetMax = maxValue * 1.1f
                val roundedMax = ((targetMax / 20f).toInt() + 1) * 20f
                
                barChart.axisLeft.apply {
                    axisMaximum = roundedMax
                    setLabelCount(5, false)
                    granularity = roundedMax / 4f
                }
            }
            else -> {
                // For Date tab: use dynamic scaling
                barChart.axisLeft.axisMaximum = maxValue * 1.1f
            }
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = "#00BCD4".toColorInt()
            setDrawValues(false)
            isHighlightEnabled = false
        }

        return BarData(dataSet)
    }

    override fun onDestroyView() {
        binding.barChart.clear()
        binding.barChart.data = null
        binding.recyclerViewMonthlyBreakdown.adapter = null
        super.onDestroyView()
    }
}

