package presentation.detail

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentTabWithChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.ReportItemAdapter
import presentation.detail.model.ChartDataWithReportItems
import presentation.detail.model.TabType

@AndroidEntryPoint
class ChartTabFragment : BaseFragment<FragmentTabWithChartBinding>(
    FragmentTabWithChartBinding::inflate
) {
    private val viewModel: ChartTabViewModel by viewModels()
    private val adapter = ReportItemAdapter()
    private var tabType: TabType = TabType.MONTHLY

    override fun initView() {
        binding.recyclerViewReportItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ChartTabFragment.adapter
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
                    setupChart(state.data.chartData)
                    adapter.submitList(state.data.reportItems)
                }
                is UIState.Error -> {}
            }
        }
    }

    private fun setupChart(chartData: ChartDataWithReportItems.ChartData) {
        val barChart = binding.barChart

        // Configure chart appearance
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
        xAxis.labelCount = chartData.labels.size

        // Set labels
        xAxis.valueFormatter = IndexAxisValueFormatter(chartData.labels)

        // Configure Y-axis (left) - in millions
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor("#E0E0E0")
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawLabels(false)
        leftAxis.valueFormatter = MillionValueFormatter()

        // Configure Y-axis (right)
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // Prepare data entries (convert to millions)
        val incomeEntries = mutableListOf<BarEntry>()
        val expenseEntries = mutableListOf<BarEntry>()

        chartData.data.forEachIndexed { index, data ->
            incomeEntries.add(BarEntry(index.toFloat(), (data.income / 1_000_000).toFloat()))
            expenseEntries.add(BarEntry(index.toFloat(), (data.expense / 1_000_000).toFloat()))
        }

        // Create datasets
        val incomeDataSet = BarDataSet(incomeEntries, "").apply {
            color = Color.parseColor("#4CAF50")
            setDrawValues(false)
        }

        val expenseDataSet = BarDataSet(expenseEntries, "").apply {
            color = Color.parseColor("#F44336")
            setDrawValues(false)
        }

        // Create grouped bar data
        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.35f
            groupBars(0f, 0.06f, 0.02f)
        }

        barChart.data = barData
        barChart.invalidate()
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

