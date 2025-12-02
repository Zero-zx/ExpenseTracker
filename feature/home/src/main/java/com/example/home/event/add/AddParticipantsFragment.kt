package com.example.home.event.add

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import base.BaseFragment
import com.example.home.databinding.FragmentAddParticipantsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddParticipantsFragment : BaseFragment<FragmentAddParticipantsBinding>(
    FragmentAddParticipantsBinding::inflate
) {

    private val viewModel: AddEventViewModel by activityViewModels()
    private val participantNames = mutableListOf<String>()

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            viewModel.navigateBack()
        }

        binding.buttonSave.setOnClickListener {
            saveParticipants()
        }

        binding.buttonAddParticipant.setOnClickListener {
            addParticipant()
        }
    }

    override fun observeData() {
        collectState(viewModel.participants) { participants ->
            participantNames.clear()
            participantNames.addAll(participants)
            updateParticipantsList()
        }
    }

    private fun addParticipant() {
        val name = binding.editTextParticipantName.text?.toString() ?: ""

        if (name.isBlank()) {
            Toast.makeText(context, "Please enter participant name", Toast.LENGTH_SHORT).show()
            return
        }

        participantNames.add(name)
        binding.editTextParticipantName.text?.clear()
        updateParticipantsList()
    }

    private fun updateParticipantsList() {
        binding.textViewParticipantsList.text = if (participantNames.isEmpty()) {
            "No participants added yet"
        } else {
            participantNames.joinToString("\n") { "• $it" }
        }
        binding.textViewCount.text = "${participantNames.size} participant(s)"
    }

    private fun saveParticipants() {
        if (participantNames.isEmpty()) {
            Toast.makeText(context, "Please add at least one participant", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.updateParticipants(participantNames.toList())
        Toast.makeText(context, "Participants saved", Toast.LENGTH_SHORT).show()
        viewModel.navigateBack()
    }
}

