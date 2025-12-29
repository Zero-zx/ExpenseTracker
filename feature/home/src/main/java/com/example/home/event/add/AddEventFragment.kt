package com.example.home.event.add

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import base.BaseFragment
import base.UIState
import com.example.home.databinding.FragmentAddEventBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEventFragment : BaseFragment<FragmentAddEventBinding>(
    FragmentAddEventBinding::inflate
) {

    private val viewModel: AddEventViewModel by activityViewModels()
    private var startDate: Long = System.currentTimeMillis()
    private var endDate: Long? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun initView() {
        binding.textViewStartDate.text = dateFormat.format(Date(startDate))
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            viewModel.navigateBack()
        }

        binding.buttonSubmit.setOnClickListener {
            createEvent()
        }

        binding.buttonSave.setOnClickListener {
            createEvent()
        }

        binding.textViewStartDate.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                binding.textViewStartDate.text = dateFormat.format(Date(startDate))
            }
        }

        binding.textViewEndDate.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                binding.textViewEndDate.text = dateFormat.format(Date(selectedDate))
            }
        }

        binding.buttonClearEndDate.setOnClickListener {
            endDate = null
            binding.textViewEndDate.text = "Optional"
        }

        binding.buttonAddParticipants.setOnClickListener {
            viewModel.navigateToAddParticipants()
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {
                    // Initial state
                }
                is UIState.Loading -> {
                    binding.buttonSubmit.isEnabled = false
                    binding.buttonSave.isEnabled = false
                }
                is UIState.Success -> {
                    Toast.makeText(
                        context,
                        "Event added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.buttonSubmit.isEnabled = true
                    binding.buttonSave.isEnabled = true
                    viewModel.navigateBack()
                }
                is UIState.Error -> {
                    Toast.makeText(
                        context,
                        "Error: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.buttonSubmit.isEnabled = true
                    binding.buttonSave.isEnabled = true
                }
            }
        }

        collectState(viewModel.participants) { participants ->
            binding.textViewParticipantCount.text = "${participants.size} participants added"
        }
    }

    private fun createEvent() {
        val eventName = binding.editTextEventName.text?.toString() ?: ""
        val numberOfParticipantsText = binding.editTextNumberOfParticipants.text?.toString() ?: "0"

        if (eventName.isBlank()) {
            Toast.makeText(context, "Please enter event name", Toast.LENGTH_SHORT).show()
            return
        }

        val numberOfParticipants = numberOfParticipantsText.toIntOrNull() ?: 0
        if (numberOfParticipants <= 0) {
            Toast.makeText(
                context,
                "Please enter valid number of participants",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (viewModel.participants.value.size != numberOfParticipants) {
            Toast.makeText(
                context,
                "Please add exactly $numberOfParticipants participants",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.addEvent(
            eventName = eventName,
            startDate = startDate,
            endDate = endDate,
            numberOfParticipants = numberOfParticipants,
            accountId = 1L // TODO: Get from shared preferences or arguments
        )
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetState()
    }
}

