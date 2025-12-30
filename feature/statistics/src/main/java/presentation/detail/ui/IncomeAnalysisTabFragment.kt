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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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

        calendar.add(Calendar.MONTH, -12)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.timeInMillis

        val startText =
            dateFormatter.format(Calendar.getInstance().apply { timeInMillis = startDate }.time)
        val endText =
            dateFormatter.format(Calendar.getInstance().apply { timeInMillis = endDate }.time)

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

            // Update chart
            updateChart(data.monthlyData)

            // Update monthly breakdown list
            monthlyAdapter.submitList(data.monthlyData)
        }
    }

    private fun updateChart(monthlyData: List<MonthlyAnalysisItem>) {
        val lineChart = binding.barChart

        // Only setup config once
        if (!isChartInitialized) {
            configureChart()
            isChartInitialized = true
        }

        // Update data
        val lineData = prepareLineData(monthlyData)
        lineChart.data = lineData
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    // 3. Configure chart appearance
    private fun configureChart() {
        val lineChart = binding.barChart

        lineChart.apply {
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

        // Configure X-axis
        lineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(true)
            axisLineColor = Color.LTGRAY
            axisLineWidth = 1f
            textColor = Color.GRAY
            textSize = 10f
            granularity = 1f

            // Set labels for 12 months
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val month = value.toInt() + 1
                    return if (month in 1..12) month.toString() else ""
                }
            }

            // Position axis at the bottom (y=0)
            setLabelCount(12, false)
            axisMinimum = 0f
            axisMaximum = 11f
        }

        // Configure Y-axis (left)
        lineChart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = "#E5E5E5".toColorInt()
            gridLineWidth = 0.5f
            setDrawAxisLine(false)
            textColor = Color.GRAY
            textSize = 10f

            // Start from 0 with no extra space
            axisMinimum = 0f
            setDrawZeroLine(false)

            // Format values in millions
            valueFormatter = MillionValueFormatter()

            // Remove spacing
            spaceTop = 10f // Small space at top for visibility
            spaceBottom = 0f
        }

        // Disable right Y-axis
        lineChart.axisRight.isEnabled = false

        // Set viewport to start from bottom
        lineChart.setViewPortOffsets(
            40f, // left padding for y-axis labels
            10f, // top padding
            10f, // right padding
            0f  // bottom padding for x-axis labels
        )
    }

    // 4. Prepare line data
    private fun prepareLineData(monthlyData: List<MonthlyAnalysisItem>): LineData {
        val lineChart = binding.barChart

        if (monthlyData.isEmpty()) {
            lineChart.setNoDataText("No Data")
            lineChart.setNoDataTextColor(Color.GRAY)
            return LineData()
        }

        val monthDataMap = monthlyData.associate {
            val parts = it.monthLabel.split("/")
            val month = parts.getOrNull(0)?.toIntOrNull() ?: 0
            month to it.amount
        }

        val entries = mutableListOf<Entry>()
        var maxValue = 0f

        (1..12).forEach { month ->
            val amount = monthDataMap[month] ?: 0.0
            val value = (amount / 1_000_000).toFloat()
            entries.add(Entry((month - 1).toFloat(), value))
            if (value > maxValue) maxValue = value
        }

        lineChart.axisLeft.axisMaximum = maxValue * 1.1f

        val dataSet = LineDataSet(entries, "").apply {
            color = "#00BCD4".toColorInt()
            lineWidth = 2f
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(false)

            // Fill without circles for ultra-clean look
            setDrawFilled(true)
            fillColor = "#00BCD4".toColorInt()
            fillAlpha = 150

            // No circles at all
            setDrawCircles(false)

            isHighlightEnabled = false
        }

        return LineData(dataSet)
    }

    override fun onDestroyView() {
        binding.barChart.clear()
        binding.barChart.data = null
        binding.recyclerViewMonthlyBreakdown.adapter = null
        super.onDestroyView()
    }
}

