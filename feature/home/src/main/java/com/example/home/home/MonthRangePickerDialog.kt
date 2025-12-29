package com.example.home.home

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.home.databinding.DialogMonthRangePickerBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ui.openMonthPicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MonthRangePickerDialog : DialogFragment() {

    private var _binding: DialogMonthRangePickerBinding? = null
    private val binding get() = _binding!!

    private var startMonthMillis: Long = 0
    private var endMonthMillis: Long = 0

    private var onRangeSelected: ((Long, Long) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMonthRangePickerBinding.inflate(layoutInflater)

        // Initialize with current default
        val calendar = Calendar.getInstance()
        endMonthMillis = calendar.timeInMillis

        // Default to start of year
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        startMonthMillis = calendar.timeInMillis

        updateDateLabels()
        setupListeners()

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Date Range")
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                onRangeSelected?.invoke(startMonthMillis, endMonthMillis)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun setupListeners() {
        binding.textStartMonth.setOnClickListener {
            openMonthPicker(binding.textStartMonth) { millis ->
                startMonthMillis = millis
                updateDateLabels()
            }
        }

        binding.textEndMonth.setOnClickListener {
            openMonthPicker(binding.textEndMonth) { millis ->
                endMonthMillis = millis
                updateDateLabels()
            }
        }
    }

    private fun updateDateLabels() {
        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        binding.textStartMonth.text = dateFormat.format(startMonthMillis)
        binding.textEndMonth.text = dateFormat.format(endMonthMillis)
    }

    fun setOnRangeSelectedListener(listener: (Long, Long) -> Unit) {
        onRangeSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): MonthRangePickerDialog {
            return MonthRangePickerDialog()
        }
    }
}

