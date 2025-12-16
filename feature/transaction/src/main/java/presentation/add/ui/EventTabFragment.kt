package presentation.add.ui

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.R as TransactionR
import com.example.transaction.databinding.FragmentEventTabBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import presentation.add.adapter.EventAdapter
import presentation.add.model.EventTabType
import presentation.add.viewModel.AddTransactionViewModel
import presentation.add.viewModel.EventSelectViewModel
import transaction.model.Event

@AndroidEntryPoint
class EventTabFragment : BaseFragment<FragmentEventTabBinding>(
    FragmentEventTabBinding::inflate
) {
    private val viewModel: EventSelectViewModel by viewModels()
    private val addTransactionViewModel: AddTransactionViewModel by hiltNavGraphViewModels(TransactionR.id.transaction_nav_graph)
    private lateinit var adapter: EventAdapter
    private var tabType: EventTabType = EventTabType.IN_PROGRESS
    private val selectedEventId: Long by lazy {
        parentFragment?.arguments?.getLong(ARG_SELECTED_EVENT_ID, -1L) ?: -1L
    }

    override fun initView() {
        adapter = EventAdapter(
            { event ->
                (parentFragment as EventSelectFragment).onEventSelected(event.id)
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
            showAddEventView()
        }

        binding.buttonSave.setOnClickListener {
            val eventName = binding.editTextEventName.text.toString()
            if (eventName.isBlank()) {
                return@setOnClickListener
            }
            
            // Create temporary event and add to AddTransactionViewModel
            val currentTime = System.currentTimeMillis()
            val event = Event(
                id = 0, // Will be assigned temporary ID
                eventName = eventName,
                startDate = currentTime,
                endDate = null,
                numberOfParticipants = 1,
                accountId = addTransactionViewModel.getCurrentAccountId() ?: 1L,
                isActive = true
            )
            addTransactionViewModel.addTemporaryEvent(event)
            
            // Clear input
            binding.editTextEventName.text?.clear()
            showRecyclerView()
        }
    }

    override fun observeData() {
        // Observe persisted events from EventSelectViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.uiState,
                addTransactionViewModel.temporaryEvents
            ) { persistedState, temporaryEvents ->
                when (persistedState) {
                    is UIState.Success -> {
                        // Merge persisted and temporary events
                        val mergedEvents = temporaryEvents + persistedState.data
                        UIState.Success(mergedEvents)
                    }
                    is UIState.Loading -> UIState.Loading
                    is UIState.Error -> persistedState
                    else -> UIState.Idle
                }
            }.collect { mergedState ->
                when (mergedState) {
                    is UIState.Loading -> {}
                    is UIState.Success -> {
                        if (mergedState.data.isEmpty()) {
                            showEmptyView()
                        } else {
                            showRecyclerView()
                            adapter.submitList(mergedState.data)
                            if (selectedEventId != -1L) {
                                val selectedEvent = mergedState.data.find { it.id == selectedEventId }
                                selectedEvent?.let {
                                    adapter.setSelectedEvents(listOf(it))
                                }
                            }
                        }
                    }
                    else -> {
                        showEmptyView()
                    }
                }
            }
        }
    }

    fun showEmptyView() {
        binding.layoutEmpty.isVisible = true
        binding.recyclerView.isVisible = false
        binding.layoutAddEvent.isVisible = false
        binding.buttonAddTrip.isVisible = tabType == EventTabType.IN_PROGRESS
    }

    fun showRecyclerView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = true
        binding.layoutAddEvent.isVisible = false
    }

    fun showAddEventView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = false
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