package presentation

import android.graphics.Color
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentStatisticsBinding>(
    FragmentStatisticsBinding::inflate
) {
    private val viewModel: ReportsViewModel by viewModels()

    override fun initListener() {
        binding.constraintLayoutIncomeAndOutcome.setOnClickListener {
            viewModel.navigateToIncomeExpenseDetail()
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {
                    // Do nothing
                }
                is UIState.Loading -> {
                    // Show loading if needed
                }
                is UIState.Success -> {
                    setupBarChart(state.data)
                }
                is UIState.Error -> {
                    // Handle error if needed
                }
            }
        }
    }

    private fun setupBarChart(chartData: IncomeExpenseChartData) {
        val barChart = binding.barChartIncomeExpense
        
        // Configure chart appearance - hide all labels
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setTouchEnabled(true)
        barChart.setDragEnabled(true)
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(false)
        barChart.legend.isEnabled = false // Hide legend
        
        // Configure X-axis - only show month numbers
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = chartData.monthlyData.size
        
        // Set month labels (just numbers: 8, 9, 10, 11, 12)
        val monthLabels = chartData.monthlyData.map { it.monthLabel }.toTypedArray()
        xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)
        
        // Configure Y-axis (left) - hide labels
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor("#E0E0E0")
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawLabels(false) // Hide Y-axis labels
        
        // Configure Y-axis (right) - disabled
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
        
        // Prepare data entries
        val incomeEntries = mutableListOf<BarEntry>()
        val expenseEntries = mutableListOf<BarEntry>()
        
        chartData.monthlyData.forEachIndexed { index, data ->
            incomeEntries.add(BarEntry(index.toFloat(), data.income.toFloat()))
            expenseEntries.add(BarEntry(index.toFloat(), data.expense.toFloat()))
        }
        
        // Create datasets - hide values on bars
        val incomeDataSet = BarDataSet(incomeEntries, "").apply {
            color = Color.parseColor("#4CAF50") // Green for income
            setDrawValues(false) // Hide values on bars
        }
        
        val expenseDataSet = BarDataSet(expenseEntries, "").apply {
            color = Color.parseColor("#F44336") // Red for expense
            setDrawValues(false) // Hide values on bars
        }
        
        // Create grouped bar data
        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.35f
            groupBars(0f, 0.06f, 0.02f)
        }
        
        barChart.data = barData
        barChart.invalidate()
    }

}

