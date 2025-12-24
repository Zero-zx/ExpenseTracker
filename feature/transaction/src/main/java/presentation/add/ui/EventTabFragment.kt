package presentation.add.ui

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentEventTabBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.EventAdapter
import presentation.add.model.EventTabType
import presentation.add.viewModel.EventSelectViewModel
import transaction.model.Event
import ui.CustomAlertDialog
import ui.showEditDialog

@AndroidEntryPoint
class EventTabFragment : BaseFragment<FragmentEventTabBinding>(
    FragmentEventTabBinding::inflate
) {
    private val viewModel: EventSelectViewModel by viewModels()
    private lateinit var adapter: EventAdapter
    private var tabType: EventTabType = EventTabType.IN_PROGRESS
    private val selectedEventName: String by lazy {
        parentFragment?.arguments?.getString(ARG_SELECTED_EVENT_NAME, "") ?: ""
    }

    override fun initView() {
        adapter = EventAdapter(
            onItemClick = { event ->
                (parentFragment as EventSelectFragment).onEventSelected(event.eventName)
            },
            onItemUpdate = { event ->
                handleEventEdit(event)
            }
        )
        binding.recyclerView.adapter = adapter

        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? EventTabType
        tabType = tabTypeArg ?: EventTabType.IN_PROGRESS

        showAddEventViewWithData(selectedEventName)
    }

    override fun initListener() {
        binding.buttonAddTrip.setOnClickListener {
            showListView()
        }

        binding.buttonSave.setOnClickListener {
            val eventName = binding.editTextEventName.text.toString()
            if (eventName.isBlank()) {
                return@setOnClickListener
            }

            (parentFragment as EventSelectFragment).onEventSelected(eventName)
        }

        binding.editTextEventName.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSearchQuery(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    override fun observeData() {
        // Observe event list from database
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> handleSuccessState(state.data)
                is UIState.Error -> handleErrorState()
                is UIState.Idle -> handleEmptyState()
            }
        }
    }

    private fun handleSuccessState(events: List<Event>) {
        handleEventListLoaded(events)
    }

    private fun handleErrorState() {
        showEmptyView()
    }

    private fun handleEmptyState() {
        if (binding.editTextEventName.text.isNullOrBlank()) showEmptyView()
    }


    private fun handleEventListLoaded(events: List<Event>) {
        showListView()
        adapter.submitList(events)
    }


    private fun handleEventEdit(event: Event) {
        showEditDialog(
            title = "Edit Event",
            inputHint = "Enter trip/event name",
            name = event.eventName,
            isCompleted = !event.isActive,
            onUpdate = { name, isCompleted ->
                viewModel.updateEvent(
                    event.copy(
                        eventName = name,
                        isActive = !isCompleted
                    )
                )
            },
            onDelete = {
                showDeleteConfirmation(event)
            }
        )
    }

    private fun showDeleteConfirmation(event: Event) {
        CustomAlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete '${event.eventName}'?")
            .setPositiveButton("Delete") { dialog ->
                viewModel.deleteEvent(event.id)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAddEventViewWithData(eventName: String) {
        showListView()
        binding.editTextEventName.setText(eventName)
    }

    fun showEmptyView() {
        binding.layoutEmpty.isVisible = true
        binding.layoutAddEvent.isVisible = false
        binding.buttonAddTrip.isVisible = tabType == EventTabType.IN_PROGRESS
    }


    fun showListView() {
        binding.layoutEmpty.isVisible = false
        binding.layoutAddEvent.isVisible = true
    }

    companion object {
        private const val ARG_TAB_TYPE = "event_tab_type"
        const val ARG_SELECTED_EVENT_NAME = "selected_event_name"

        fun newInstance(tabType: EventTabType): EventTabFragment {
            return EventTabFragment().apply {
                arguments = android.os.Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }
}