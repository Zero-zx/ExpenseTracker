package presentation

import android.graphics.Color
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentReportsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentReportsBinding>(
    FragmentReportsBinding::inflate
) {
    private val viewModel: ReportsViewModel by viewModels()
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun initView() {
        // Set rounded corners for the combined chart: 8dp radius
        val radiusPx = (8 * resources.displayMetrics.density).toInt()
        binding.barChartIncomeExpense.setRadius(radiusPx)
    }

    override fun initListener() {
        binding.constraintLayoutIncomeAndOutcome.setOnClickListener {
            viewModel.navigateToExpenseVsIncome()
        }
        
        binding.constraintLayoutCurrentFinance.setOnClickListener {
            viewModel.navigateToReportDetailContainer()
        }

        binding.buttonExpenseAnalysis.setOnClickListener {
            viewModel.navigateToExpenseAnalysis()
        }

        binding.buttonIncomeAnalysis.setOnClickListener {
            viewModel.navigateToIncomeAnalysis()
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
                    updateFinancialSummary(state.data)
                    setupBarChart(state.data)
                }
                is UIState.Error -> {
                    // Handle error if needed
                }
            }
        }
    }

    private fun updateFinancialSummary(chartData: IncomeExpenseChartData) {
        binding.apply {
            // Update total income
            textViewTotalIncome.text = currencyFormatter.format(chartData.totalIncome)

            // Update total expense
            textViewTotalOutcome.text = currencyFormatter.format(chartData.totalExpense)

            // Update balance (income - expense)
            textViewBalance.text = currencyFormatter.format(chartData.balance)
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

        val monthLabels = chartData.monthlyData.map { it.monthLabel }.toTypedArray()
        xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)

        // Prepare data entries for combined chart
        // Dataset 0 (X) = Expense (bottom, red)
        // Dataset 1 (Y) = Income (top, green)
        val expenseEntries = mutableListOf<BarEntry>()
        val incomeEntries = mutableListOf<BarEntry>()
        var maxTotal = 0.0

        chartData.monthlyData.forEachIndexed { index, data ->
            expenseEntries.add(BarEntry(index.toFloat(), data.expense.toFloat()))
            incomeEntries.add(BarEntry(index.toFloat(), data.income.toFloat()))
            // Track the maximum combined value for Y-axis scaling
            val total = data.expense + data.income
            if (total > maxTotal) maxTotal = total
        }

        // Configure Y-axis (left) - set maximum to the highest combined value
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor("#E0E0E0")
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = (maxTotal * 1.1).toFloat() // Add 10% padding at top
        leftAxis.setDrawLabels(false) // Hide Y-axis labels

        // Configure Y-axis (right) - disabled
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // Create dataset for expense (bottom, red) - Dataset 0
        val expenseDataSet = BarDataSet(expenseEntries, "").apply {
            color = Color.parseColor("#F44336") // Red for expense
            setDrawValues(false) // Hide values on bars
        }

        // Create dataset for income (top, green) - Dataset 1
        val incomeDataSet = BarDataSet(incomeEntries, "").apply {
            color = Color.parseColor("#4CAF50") // Green for income
            setDrawValues(false) // Hide values on bars
        }

        // Create bar data with both datasets - RoundedCombinedBarChart will combine them
        val barData = BarData(expenseDataSet, incomeDataSet).apply {
            barWidth = 0.6f
        }

        barChart.data = barData
        barChart.invalidate()
    }

}

