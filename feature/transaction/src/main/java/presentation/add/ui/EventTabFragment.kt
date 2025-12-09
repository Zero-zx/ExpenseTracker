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

@AndroidEntryPoint
class EventTabFragment : BaseFragment<FragmentEventTabBinding>(
    FragmentEventTabBinding::inflate
) {
    private val viewModel: EventSelectViewModel by viewModels()
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
            viewModel.addEvent(eventName)
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {
                    // Clear the input field after successful add
                    binding.editTextEventName.text?.clear()

                    if (state.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        showRecyclerView()
                        adapter.submitList(state.data)
                        if (selectedEventId != -1L) {
                            val selectedEvent = state.data.find { it.id == selectedEventId }
                            adapter.setSelectedEvents(listOf(selectedEvent!!))
                        }
                    }
                }

                else -> {
                    showEmptyView()
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