package presentation

import account.model.Account
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.statistics.R
import com.example.statistics.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>(
    FragmentStatisticsBinding::inflate
) {
    private val viewModel: StatisticsViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun initView() {
        setupChart()
        setupAccountSelector()
    }

    override fun initListener() {
        binding.buttonSelectStartDate.setOnClickListener {
            showDatePicker(true)
        }

        binding.buttonSelectEndDate.setOnClickListener {
            showDatePicker(false)
        }

        binding.buttonApplyFilter.setOnClickListener {
            val state = viewModel.statisticsState.value
            viewModel.setDateRange(state.startDate, state.endDate)
        }
    }

    override fun observeData() {
        collectState(viewModel.statisticsState) { state ->
            updateDateButtons(state.startDate, state.endDate)
            updateAccountSelector(state.selectedAccount, state.accounts)
        }

        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> showLoading()
                is UIState.Idle -> {}
                is UIState.Success -> {
                    hideLoading()
                    val statisticsState = state.data
                    updateChart(statisticsState.chartData)
                }
                is UIState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
            }
        }
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setPinchZoom(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    private fun updateChart(chartData: ChartData?) {
        if (chartData == null || chartData.entries.isEmpty()) {
            binding.lineChart.data = null
            binding.lineChart.invalidate()
            binding.textViewEmptyChart.visibility = View.VISIBLE
            binding.lineChart.visibility = View.GONE
            return
        }

        binding.textViewEmptyChart.visibility = View.GONE
        binding.lineChart.visibility = View.VISIBLE

        val primaryColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_color)
        val textColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary)
        
        val dataSet = LineDataSet(chartData.entries, "Transactions").apply {
            color = primaryColor
            valueTextColor = textColor
            lineWidth = 2f
            setCircleColor(primaryColor)
            circleRadius = 4f
            setDrawCircleHole(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            setDrawFilled(true)
            fillColor = primaryColor
            fillAlpha = 50
        }

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData

        // Set custom formatter for X-axis labels
        binding.lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < chartData.labels.size) {
                    chartData.labels[index]
                } else {
                    ""
                }
            }
        }

        binding.lineChart.invalidate()
    }

    private fun setupAccountSelector() {
        binding.spinnerAccount.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val state = viewModel.statisticsState.value
                if (position < state.accounts.size) {
                    viewModel.selectAccount(state.accounts[position])
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun updateAccountSelector(selectedAccount: Account?, accounts: List<Account>) {
        if (accounts.isEmpty()) return

        val adapter = AccountSpinnerAdapter(requireContext(), accounts)
        binding.spinnerAccount.adapter = adapter

        selectedAccount?.let { account ->
            val position = accounts.indexOfFirst { it.id == account.id }
            if (position >= 0) {
                binding.spinnerAccount.setSelection(position)
            }
        }
    }

    private fun updateDateButtons(startDate: Long, endDate: Long) {
        binding.buttonSelectStartDate.text = dateFormat.format(startDate)
        binding.buttonSelectEndDate.text = dateFormat.format(endDate)
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val state = viewModel.statisticsState.value
        val initialDate = if (isStartDate) state.startDate else state.endDate

        val calendar = Calendar.getInstance().apply {
            timeInMillis = initialDate
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                    if (isStartDate) {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    } else {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }
                }

                val currentState = viewModel.statisticsState.value
                if (isStartDate) {
                    if (selectedCalendar.timeInMillis <= currentState.endDate) {
                        viewModel.setDateRange(selectedCalendar.timeInMillis, currentState.endDate)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Start date must be before end date",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (selectedCalendar.timeInMillis >= currentState.startDate) {
                        viewModel.setDateRange(currentState.startDate, selectedCalendar.timeInMillis)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "End date must be after start date",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.lineChart.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.lineChart.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}

