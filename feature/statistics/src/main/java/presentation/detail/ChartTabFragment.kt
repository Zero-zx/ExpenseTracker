package presentation.detail

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.common.R
import com.example.statistics.databinding.FragmentTabWithChartBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
        
        // Update data
        val barData = prepareBarData(chartData)
        barChart.data = barData
        barChart.animateY(500) // Animate when data changes
        barChart.invalidate()
    }

    private fun configureChart() {
        // Configure chart appearance - only once
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setTouchEnabled(true)
        barChart.setDragEnabled(true)
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(false)
        barChart.legend.isEnabled = false

        // Configure X-axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        // Configure Y-axis (left) - in millions
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor("#E0E0E0") // Use existing gray color
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawLabels(false)
        leftAxis.valueFormatter = MillionValueFormatter()

        // Configure Y-axis (right)
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun prepareBarData(chartData: ChartDataWithReportItems.ChartData): BarData {
        // Configure X-axis labels
        barChart.xAxis.labelCount = chartData.labels.size
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(chartData.labels)

        // Prepare data entries (convert to millions)
        val incomeEntries = mutableListOf<BarEntry>()
        val expenseEntries = mutableListOf<BarEntry>()

        chartData.data.forEachIndexed { index, data ->
            incomeEntries.add(BarEntry(index.toFloat(), (data.income / 1_000_000).toFloat()))
            expenseEntries.add(BarEntry(index.toFloat(), (data.expense / 1_000_000).toFloat()))
        }

        // Create datasets with colors from resources
        val incomeDataSet = BarDataSet(incomeEntries, "").apply {
            color = ContextCompat.getColor(requireContext(), R.color.green_income)
            setDrawValues(false)
        }

        val expenseDataSet = BarDataSet(expenseEntries, "").apply {
            color = ContextCompat.getColor(requireContext(), R.color.red_expense)
            setDrawValues(false)
        }

        // Create grouped bar data
        return BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.35f
            groupBars(0f, 0.06f, 0.02f)
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

