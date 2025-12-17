package presentation.add.ui

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentEventTabBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.EventAdapter
import presentation.add.model.EventTabType
import presentation.add.viewModel.AddTransactionViewModel
import presentation.add.viewModel.EventSelectViewModel
import transaction.model.Event
import ui.navigateBack
import com.example.transaction.R as TransactionR

@AndroidEntryPoint
class EventTabFragment : BaseFragment<FragmentEventTabBinding>(
    FragmentEventTabBinding::inflate
) {
    private val viewModel: EventSelectViewModel by viewModels()
    private val addTransactionViewModel: AddTransactionViewModel by hiltNavGraphViewModels(
        TransactionR.id.transaction_nav_graph
    )
    private lateinit var adapter: EventAdapter
    private var tabType: EventTabType = EventTabType.IN_PROGRESS
    private val selectedEventId: Long by lazy {
        parentFragment?.arguments?.getLong(ARG_SELECTED_EVENT_ID, -1L) ?: -1L
    }

    override fun initView() {
        adapter = EventAdapter(
            { event ->
                addTransactionViewModel.selectEvent(event)
                navigateBack()
            },
            {
                // TODO: Handle item update
            }
        )
        binding.recyclerView.adapter = adapter

        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? EventTabType
        tabType = tabTypeArg ?: EventTabType.IN_PROGRESS
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

            // Create temporary event and add to AddTransactionViewModel
            val currentTime = System.currentTimeMillis()
            val event = Event(
                id = -1L,
                eventName = eventName,
                startDate = currentTime,
                endDate = null,
                numberOfParticipants = 1,
                accountId = addTransactionViewModel.getCurrentAccountId() ?: 1L,
                isActive = true
            )
            addTransactionViewModel.selectEvent(event)

            navigateBack()
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

        // Observe selected event from AddTransactionViewModel
        collectState(addTransactionViewModel.selectedEvent) { selectedEvent ->
            selectedEvent?.let { event ->
                handleSelectedEvent(event)
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

        val selectedEvent = addTransactionViewModel.selectedEvent.value
        if (selectedEvent != null) {
            val eventInDatabase = events.find { it.id == selectedEvent.id }
            if (eventInDatabase != null) {
                // Event exists in database - select it in the list
                adapter.setSelectedEvents(listOf(eventInDatabase))
            } else {
                // Event doesn't exist in database - show add form
                showAddEventViewWithData(selectedEvent.eventName)
            }
        } else if (selectedEventId != -1L) {
            // Handle pre-selected event from arguments
            events.find { it.id == selectedEventId }?.let { event ->
                adapter.setSelectedEvents(listOf(event))
            }
        }
    }

    private fun handleSelectedEvent(event: Event) {
        binding.layoutAddEvent.isVisible = true
        binding.editTextEventName.setText(event.eventName)
    }

    private fun isEventInDatabase(event: Event): Boolean {
        return event.id > 0
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
        const val ARG_SELECTED_EVENT_ID = "selected_event_id"

        fun newInstance(tabType: EventTabType): EventTabFragment {
            return EventTabFragment().apply {
                arguments = android.os.Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }
}