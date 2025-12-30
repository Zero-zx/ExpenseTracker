package com.example.home.home

import android.graphics.Color
import android.view.View
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.home.databinding.FragmentHomeBinding
import com.example.home.home.model.HomeReportData
import com.example.home.home.usecase.CategoryExpenseData
import com.example.home.home.usecase.MonthlyExpenseData
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import ui.gone
import ui.showNotImplementToast
import ui.visible
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()
    private var selectedTimePeriod = TimePeriod.THIS_MONTH

    // Colors for pie chart categories
    private val categoryColors = listOf(
        Color.parseColor("#FFC107"), // Yellow
        Color.parseColor("#F44336"), // Red
        Color.parseColor("#03A9F4")  // Light Blue
    )

    override fun initView() {
        setupBarChart()
        setupPieChart()
    }

    override fun initListener() {
        binding.apply {
            buttonArrow.setOnClickListener {
                viewModel.navigateToReportDetailContainer()
            }

            iconRefresh.setOnClickListener {
                viewModel.loadTransactionData(selectedTimePeriod)
            }

            iconNotification.setOnClickListener {
                showNotImplementToast()
            }

            iconEye.setOnClickListener {
                // Toggle balance visibility
            }

            buttonSettings.setOnClickListener {
                showNotImplementToast()
            }

            buttonTimePeriod.setOnClickListener {
                showTimePeriodPopup()
            }

            buttonRecordHistory.setOnClickListener {
                viewModel.navigateToTransaction()
            }

            layoutBudget.setOnClickListener {
                showNotImplementToast()
            }

            layoutSavingAccount.setOnClickListener {
                showNotImplementToast()
            }
        }
    }

    override fun observeData() {
        // Observe total balance
        collectState(viewModel.totalBalance) { totalBalance ->
            binding.textViewBalance.text = formatCurrency(totalBalance)
        }

        // Observe HomeReportData và update charts
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {}
                is UIState.Loading -> {
                    // Could show loading indicator if needed
                }
                is UIState.Success -> {
                    updateUI(state.data)
                }
                is UIState.Error -> {
                    // Handle error if needed
                }
            }
        }

        // Observe monthly expense data for expense analysis chart
        collectState(viewModel.monthlyExpenseData) { monthlyData ->
            setupExpenseAnalysisChart(monthlyData)
        }
    }

    private fun updateUI(data: HomeReportData) {
        if (!data.hasData) {
            showNoDataState()
            return
        }

        hideNoDataState()

        // Update income, expense, difference
        binding.textIncome.text = formatCurrency(data.income)
        binding.textExpense.text = formatCurrency(data.expense)
        binding.textDifference.text = formatCurrency(data.difference)

        // Update bar chart
        updateBarChart(data.income, data.expense)

        // Update pie chart and category list
        updatePieChart(data.topCategories)
        updateCategoryList(data.topCategories)
    }

    private fun showNoDataState() {
        binding.textNoData.visible()
        binding.layoutCharts.gone()
        binding.layoutCategoryList.gone()
    }

    private fun hideNoDataState() {
        binding.textNoData.gone()
        binding.layoutCharts.visible()
        binding.layoutCategoryList.visible()
    }

    private fun setupBarChart() {
        val barChart = binding.barChart

        // Configure chart appearance
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setTouchEnabled(false)
        barChart.setDragEnabled(false)
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.legend.isEnabled = false

        // Configure X-axis - hide it
        val xAxis = barChart.xAxis
        xAxis.isEnabled = false

        // Configure Y-axis (left) - hide labels
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawLabels(false)
        leftAxis.axisMinimum = 0f

        // Configure Y-axis (right) - disabled
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun updateBarChart(income: Double, expense: Double) {
        val barChart = binding.barChart

        // Find max value for scaling
        val maxValue = maxOf(income, expense, 1.0) // At least 1 to avoid division by zero

        // Create entries - normalize to 0-1 range for display
        val incomeEntry = BarEntry(0f, (income / maxValue).toFloat())
        val expenseEntry = BarEntry(1f, (expense / maxValue).toFloat())

        // Create datasets
        val incomeDataSet = BarDataSet(listOf(incomeEntry), "").apply {
            color = Color.parseColor("#10B981") // Green for income
            setDrawValues(false)
        }

        val expenseDataSet = BarDataSet(listOf(expenseEntry), "").apply {
            color = Color.parseColor("#F44336") // Red for expense
            setDrawValues(false)
        }

        // Create bar data
        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.8f
        }

        //remove Y axis line
        barChart.axisLeft.setDrawAxisLine(false)

        barChart.data = barData
        barChart.invalidate()
    }

    private fun setupPieChart() {
        val pieChart = binding.pieChart

        // Configure chart appearance
        pieChart.description.isEnabled = false
        pieChart.setDrawHoleEnabled(true)
        pieChart.holeRadius = 50f
        pieChart.transparentCircleRadius = 55f
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.legend.isEnabled = false
    }

    private fun updatePieChart(categories: List<CategoryExpenseData>) {
        val pieChart = binding.pieChart

        // Always show 3 segments (fill with 0% if needed)
        val entries = categories.take(3).map { categoryData ->
            PieEntry(categoryData.percentage.toFloat(), "")
        }

        // Create dataset
        val dataSet = PieDataSet(entries, "").apply {
            colors = categoryColors
            setDrawValues(false)
            sliceSpace = 2f
        }

        pieChart.data = PieData(dataSet)
        pieChart.invalidate()
    }

    private fun updateCategoryList(categories: List<CategoryExpenseData>) {
        val categoryLayouts = listOf(
            binding.layoutCategory1 to Triple(binding.viewColor1, binding.textCategory1, binding.textPercentage1),
            binding.layoutCategory2 to Triple(binding.viewColor2, binding.textCategory2, binding.textPercentage2),
            binding.layoutCategory3 to Triple(binding.viewColor3, binding.textCategory3, binding.textPercentage3)
        )

        categories.forEachIndexed { index, categoryData ->
            if (index < categoryLayouts.size) {
                val (layout, views) = categoryLayouts[index]
                val (colorView, nameView, percentageView) = views

                if (categoryData.category != null && categoryData.percentage > 0) {
                    layout.visible()
                    colorView.setBackgroundColor(categoryColors[index])
                    nameView.text = categoryData.category.title
                    percentageView.text = String.format(Locale.getDefault(), "%.2f %%", categoryData.percentage)
                } else {
                    // Show category with 0%
                    layout.visible()
                    colorView.setBackgroundColor(categoryColors[index])
                    nameView.text = "-" // Or you could show a default category name
                    percentageView.text = "0 %"
                }
            }
        }
    }

    private fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.maximumFractionDigits = 0
        return "${formatter.format(amount)} ₫"
    }

    private fun showTimePeriodPopup() {
        val timePeriods = TimePeriod.values()

        // Cycle through periods on click
        val currentIndex = timePeriods.indexOf(selectedTimePeriod)
        val nextIndex = (currentIndex + 1) % timePeriods.size
        selectedTimePeriod = timePeriods[nextIndex]
        binding.textTimePeriod.text = selectedTimePeriod.displayName
        viewModel.loadTransactionData(selectedTimePeriod)
    }

    private fun setupExpenseAnalysisChart(monthlyData: List<MonthlyExpenseData>) {
        val barChart = binding.expenseAnalysisChart

        if (monthlyData.isEmpty()) {
            barChart.data = null
            barChart.invalidate()
            return
        }

        // Configure chart appearance
        barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(false)
            setDrawBorders(false)
            legend.isEnabled = false
        }

        // Configure X-axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        xAxis.labelCount = monthlyData.size

        val monthLabels = monthlyData.map { it.getMonthLabel() }.toTypedArray()
        xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)

        // Create entries
        val entries = monthlyData.mapIndexed { index, data ->
            BarEntry(index.toFloat(), (data.amount / 1_000).toFloat()) // Convert to thousands
        }

        // Configure Y-axis
        val maxValue = monthlyData.maxOfOrNull { it.amount } ?: 0.0
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = ((maxValue / 1_000) * 1.1).toFloat() // Add 10% padding
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}K"
            }
        }

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // Create dataset
        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#00BCD4")
            setDrawValues(false)
        }

        // Create bar data
        val barData = BarData(dataSet).apply {
            barWidth = 0.4f
        }

        barChart.data = barData
        barChart.invalidate()
    }
}