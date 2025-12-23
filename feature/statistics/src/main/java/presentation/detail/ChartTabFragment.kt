package presentation.detail

import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.common.R
import com.example.statistics.databinding.FragmentTabWithChartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.ChartHeaderAdapter
import presentation.detail.adapter.ReportItemAdapter
import presentation.detail.model.ChartDataWithReportItems
import presentation.detail.model.TabType

@AndroidEntryPoint
class ChartTabFragment : BaseFragment<FragmentTabWithChartBinding>(
    FragmentTabWithChartBinding::inflate
) {
    private val viewModel: ChartTabViewModel by viewModels()
    private val reportItemAdapter = ReportItemAdapter()
    private var tabType: TabType = TabType.MONTHLY
    private var isChartInitialized = false
    private lateinit var barChart: BarChart

    override fun initView() {
        // Create BarChart programmatically
        barChart = BarChart(requireContext())
        barChart.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            (300 * resources.displayMetrics.density).toInt()
        )

        // Setup RecyclerView with ConcatAdapter
        val chartHeaderAdapter = ChartHeaderAdapter(barChart)
        val concatAdapter = ConcatAdapter(chartHeaderAdapter, reportItemAdapter)

        binding.recyclerViewReportItems.apply {
            if (layoutManager == null) {
                layoutManager = LinearLayoutManager(requireContext())
            }
            adapter = concatAdapter
        }

        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? TabType
        tabType = tabTypeArg ?: TabType.MONTHLY
        viewModel.loadData(tabType)
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {}
                is UIState.Loading -> {}
                is UIState.Success -> {
                    updateChart(state.data.chartData)
                    reportItemAdapter.submitList(state.data.reportItems)
                }

                is UIState.Error -> {}
            }
        }
    }

    private fun updateChart(chartData: ChartDataWithReportItems.ChartData) {
        // Only setup config once
        if (!isChartInitialized) {
            configureChart()
            isChartInitialized = true
        }

        // Configure zero line based on chart style
        val isStackedStyle = tabType == TabType.MONTHLY
        barChart.axisLeft.apply {
            setDrawZeroLine(isStackedStyle)
            if (isStackedStyle) {
                zeroLineColor = "#999999".toColorInt()
                zeroLineWidth = 1.5f
            }
        }

        // Update data
        val barData = prepareBarData(chartData)
        barChart.data = barData
        barChart.animateY(500)
        barChart.invalidate()
    }

    private fun configureChart() {
        // Configure chart appearance
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBorders(false)
        barChart.setTouchEnabled(true)
        barChart.setDragEnabled(true)
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(false)
        barChart.legend.isEnabled = true

        // Configure legend
        barChart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            textColor = "#666666".toColorInt()
        }

        // Configure X-axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        xAxis.textColor = "#666666".toColorInt()

        // Configure Y-axis (left) - configuration will be updated based on chart style
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = "#E0E0E0".toColorInt()
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(true)
        leftAxis.textColor = "#666666".toColorInt()
        leftAxis.valueFormatter = ThousandValueFormatter()

        // Configure Y-axis (right)
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun prepareBarData(chartData: ChartDataWithReportItems.ChartData): BarData {
        // Determine chart style based on TabType
        val isStackedStyle = tabType == TabType.MONTHLY

        // Prepare data entries based on TabType
        val incomeEntries = mutableListOf<BarEntry>()
        val expenseEntries = mutableListOf<BarEntry>()

        var maxIncome = 0f
        var maxExpense = 0f

        chartData.data.forEachIndexed { index, data ->
            // Convert to thousands (not millions)
            val incomeValue = (data.income / 1_000).toFloat()
            val expenseValue = (data.expense / 1_000).toFloat()

            if (isStackedStyle) {
                // MONTHLY: Stacked style - income positive, expense negative
                incomeEntries.add(BarEntry(index.toFloat(), incomeValue))
                expenseEntries.add(BarEntry(index.toFloat(), -expenseValue)) // Make negative
            } else {
                // QUARTER, YEAR, CUSTOM: Side-by-side style - both positive
                incomeEntries.add(BarEntry(index.toFloat(), incomeValue))
                expenseEntries.add(BarEntry(index.toFloat(), expenseValue)) // Keep positive
            }

            if (incomeValue > maxIncome) maxIncome = incomeValue
            if (expenseValue > maxExpense) maxExpense = expenseValue
        }

        // Set Y-axis range based on chart style
        if (isStackedStyle) {
            // Stacked style: balanced range for positive/negative
            val maxValue = maxOf(maxIncome, maxExpense) * 1.1f
            barChart.axisLeft.axisMaximum = maxValue
            barChart.axisLeft.axisMinimum = -maxValue
        } else {
            // Side-by-side style: only positive range
            val maxValue = maxOf(maxIncome, maxExpense) * 1.2f
            barChart.axisLeft.axisMaximum = maxValue
            barChart.axisLeft.axisMinimum = 0f
        }

        // Create datasets
        val incomeDataSet = BarDataSet(incomeEntries, "Income").apply {
            color = ContextCompat.getColor(requireContext(), R.color.green_income)
            setDrawValues(false)
        }

        val expenseDataSet = BarDataSet(expenseEntries, "Expense").apply {
            color = ContextCompat.getColor(requireContext(), R.color.red_expense)
            setDrawValues(false)
        }

        // Create bar data with appropriate grouping
        val barData = BarData(incomeDataSet, expenseDataSet)

        if (isStackedStyle) {
            // Stacked style - single bar per index
            barData.barWidth = 0.2f

            // Configure X-axis for stacked style
            barChart.xAxis.apply {
                labelCount = chartData.labels.size
                valueFormatter = IndexAxisValueFormatter(chartData.labels)
                axisMinimum = -0.5f
                axisMaximum = chartData.labels.size - 0.5f
                setCenterAxisLabels(false)
                granularity = 1f
            }
        } else {
            // Side-by-side bars - grouped
            val barWidth = 0.35f
            val groupSpace = 0.2f
            val barSpace = 0.05f

            barData.barWidth = barWidth

            // Configure X-axis for grouped bars
            // Total group width = barWidth * 2 (number of bars) + barSpace * 1 (spaces between bars) + groupSpace
            // = 0.35 * 2 + 0.05 + 0.2 = 0.95
            barChart.xAxis.apply {
                labelCount = chartData.labels.size
                valueFormatter = IndexAxisValueFormatter(chartData.labels)
                axisMinimum = 0f
                axisMaximum = chartData.labels.size.toFloat()
                setCenterAxisLabels(true)
                granularity = 1f
                // Center the labels at the group positions
                setLabelCount(chartData.labels.size, false)
            }

            // Group the bars - this adjusts x-positions
            barData.groupBars(0f, groupSpace, barSpace)
        }

        return barData
    }

    // Value formatter for Y-axis (shows values in thousands)
    class ThousandValueFormatter : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return if (value == 0f) {
                "0"
            } else {
                "${value.toInt()}"
            }
        }
    }

    override fun onDestroyView() {
        barChart.clear()
        barChart.data = null
        binding.recyclerViewReportItems.adapter = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"

        fun newInstance(tabType: TabType): ChartTabFragment {
            return ChartTabFragment().apply {
                arguments = android.os.Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }
}

