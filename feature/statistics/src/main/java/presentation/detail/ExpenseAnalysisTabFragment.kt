package presentation.detail

import android.graphics.Color
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentTabAnalysisBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.MonthlyAnalysisAdapter
import presentation.detail.model.AnalysisData
import presentation.detail.model.MonthlyAnalysisItem
import presentation.detail.model.TabType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ExpenseAnalysisTabFragment : BaseFragment<FragmentTabAnalysisBinding>(
    FragmentTabAnalysisBinding::inflate
) {
    private val viewModel: ExpenseAnalysisViewModel by viewModels()
    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("MM/yyyy", Locale.getDefault())

    private val monthlyAdapter = MonthlyAnalysisAdapter(
        onItemClick = { item ->
            // TODO: Navigate to monthly detail
        },
        isExpense = true
    )

    private var tabType: TabType = TabType.MONTHLY
    private var isChartInitialized = false

    companion object {
        private const val ARG_TAB_TYPE = "tab_type"

        fun newInstance(tabType: TabType): ExpenseAnalysisTabFragment {
            return ExpenseAnalysisTabFragment().apply {
                arguments = android.os.Bundle().apply {
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
            // TODO: Navigate to category selection
        }

        // Account filter
        binding.layoutAccountFilter.setOnClickListener {
            // TODO: Navigate to account selection
        }

        updateDateRangeDisplay()
    }

    private fun updateDateRangeDisplay() {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, -12)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.timeInMillis

        val startText = dateFormatter.format(Calendar.getInstance().apply { timeInMillis = startDate }.time)
        val endText = dateFormatter.format(Calendar.getInstance().apply { timeInMillis = endDate }.time)

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
                    // TODO: Handle error
                }
            }
        }
    }

    private fun updateUI(data: AnalysisData) {
        binding.apply {
            // Update summary
            textViewTotalExpense.text = currencyFormatter.format(data.totalAmount)
            textViewAverageExpense.text = currencyFormatter.format(data.averagePerMonth)

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
        barChart.animateY(500) // Animate when data changes
        barChart.invalidate()
    }

    private fun configureChart() {
        val barChart = binding.barChart

        // Configure chart appearance - only once
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setTouchEnabled(true)
        barChart.setDragEnabled(true)
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(false)
        barChart.legend.isEnabled = false

        // Configure X-axis - show month numbers (1-12)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        // Create labels for 12 months
        val monthLabels = (1..12).map { it.toString() }.toTypedArray()
        xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)
        xAxis.labelCount = 12

        // Configure Y-axis (left) - in millions
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor("#E0E0E0")
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawLabels(true)
        leftAxis.valueFormatter = MillionValueFormatter()

        // Configure Y-axis (right)
        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun prepareBarData(monthlyData: List<MonthlyAnalysisItem>): BarData {
        val barChart = binding.barChart

        // Show "No Data" message if no data
        if (monthlyData.isEmpty()) {
            barChart.setNoDataText("No Data")
            barChart.setNoDataTextColor(Color.GRAY)
        }

        // Prepare data entries - convert to millions and map to month indices
        val entries = mutableListOf<BarEntry>()
        val monthDataMap = monthlyData.associate {
            val parts = it.monthLabel.split("/")
            val month = parts.getOrNull(0)?.toIntOrNull() ?: run {
                android.util.Log.w("ExpenseAnalysis", "Invalid monthLabel format: ${it.monthLabel}")
                0
            }
            month to it.amount
        }

        // Create entries for all 12 months
        (1..12).forEach { month ->
            val amount = monthDataMap[month] ?: 0.0
            entries.add(BarEntry((month - 1).toFloat(), (amount / 1_000_000).toFloat()))
        }

        // Create dataset with color constant
        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#F44336") // Red color for expense bars
            setDrawValues(false)
        }

        return BarData(dataSet).apply {
            barWidth = 0.6f
        }
    }

    override fun onDestroyView() {
        binding.barChart.clear()
        binding.barChart.data = null
        binding.recyclerViewMonthlyBreakdown.adapter = null
        super.onDestroyView()
    }
}

