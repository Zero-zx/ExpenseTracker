package com.example.home.home

import android.graphics.Color
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.home.databinding.FragmentHomeBinding
import com.example.home.home.model.HomeReportData
import com.example.home.home.usecase.CategoryExpenseData
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import ui.gone
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
                // Navigate to accounts or balance detail
            }

            iconRefresh.setOnClickListener {
                // Refresh data
                viewModel.loadTransactionData(selectedTimePeriod)
            }

            iconNotification.setOnClickListener {
                // Open notifications
            }

            iconEye.setOnClickListener {
                // Toggle balance visibility
            }

            buttonSettings.setOnClickListener {
                // Open expense vs income settings
            }

            buttonTimePeriod.setOnClickListener {
                // Show time period selection popup
                showTimePeriodPopup()
            }

            buttonRecordHistory.setOnClickListener {
                // Navigate to record history
                viewModel.navigateToTransaction()
            }
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {
                    showNoDataState()
                }
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

        // Configure Y-axis (left) - completely remove
        val leftAxis = barChart.axisLeft
        leftAxis.isEnabled = false
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawLabels(false)
        leftAxis.setDrawAxisLine(false)

        // Configure Y-axis (right) - completely remove
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)
    }

    private fun updateBarChart(income: Double, expense: Double) {
        val barChart = binding.barChart

        // Find max value for scaling
        val maxValue = maxOf(income, expense, 1.0) // At least 1 to avoid division by zero

        // Minimum visible height as percentage (e.g., 5% of max value)
        val minVisibleHeight = 0.01f

        // Create entries - normalize to 0-1 range for display, with minimum height
        val incomeNormalized = (income / maxValue).toFloat()
        val expenseNormalized = (expense / maxValue).toFloat()

        // Apply minimum height: if value is 0, show minVisibleHeight; otherwise show actual value
        val incomeEntry = BarEntry(
            0f,
            if (income == 0.0) minVisibleHeight else maxOf(incomeNormalized, minVisibleHeight)
        )
        val expenseEntry = BarEntry(
            1f,
            if (expense == 0.0) minVisibleHeight else maxOf(expenseNormalized, minVisibleHeight)
        )

        // Create datasets
        val incomeDataSet = BarDataSet(listOf(incomeEntry), "").apply {
            color = Color.parseColor("#10B981") // Green for income
            setDrawValues(false)
        }

        val expenseDataSet = BarDataSet(listOf(expenseEntry), "").apply {
            color = Color.parseColor("#F44336") // Red for expense
            setDrawValues(false)
        }

        val barWidth = 0.9f
        val barSpace = 0.2f
        val groupSpace = 0f

        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            this.barWidth = barWidth
        }

        barChart.data = barData
        barChart.groupBars(-0.6f, groupSpace, barSpace)
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
            binding.layoutCategory1 to Triple(
                binding.viewColor1,
                binding.textCategory1,
                binding.textPercentage1
            ),
            binding.layoutCategory2 to Triple(
                binding.viewColor2,
                binding.textCategory2,
                binding.textPercentage2
            ),
            binding.layoutCategory3 to Triple(
                binding.viewColor3,
                binding.textCategory3,
                binding.textPercentage3
            )
        )

        categories.forEachIndexed { index, categoryData ->
            if (index < categoryLayouts.size) {
                val (layout, views) = categoryLayouts[index]
                val (colorView, nameView, percentageView) = views

                if (categoryData.category != null && categoryData.percentage > 0) {
                    layout.visible()
                    colorView.setBackgroundColor(categoryColors[index])
                    nameView.text = categoryData.category.title
                    percentageView.text =
                        String.format(Locale.getDefault(), "%.2f %%", categoryData.percentage)
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

        val popupView = LayoutInflater.from(requireContext()).inflate(
            android.R.layout.simple_list_item_1,
            null
        )

        val popupWindow = PopupWindow(
            popupView,
            binding.buttonTimePeriod.width,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Show popup below the button
        popupWindow.showAsDropDown(binding.buttonTimePeriod, 0, 8)

        // TODO: Implement proper popup with custom layout showing time periods
        // For now, cycle through periods on click
        val currentIndex = timePeriods.indexOf(selectedTimePeriod)
        val nextIndex = (currentIndex + 1) % timePeriods.size
        selectedTimePeriod = timePeriods[nextIndex]
        binding.textTimePeriod.text = selectedTimePeriod.displayName
        viewModel.loadTransactionData(selectedTimePeriod)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTransactionData(selectedTimePeriod)
    }
}