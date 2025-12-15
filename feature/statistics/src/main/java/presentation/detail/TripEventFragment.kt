package presentation.detail

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.TabConfig
import base.UIState
import base.setupWithTabs
import com.example.statistics.databinding.FragmentTripEventBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.TripEventAdapter
import presentation.detail.model.TripEventData

@AndroidEntryPoint
class TripEventFragment : BaseFragment<FragmentTripEventBinding>(
    FragmentTripEventBinding::inflate
) {
    private val viewModel: TripEventViewModel by viewModels()
    private val adapter = TripEventAdapter { tripEventData ->
        // TODO: Navigate to event detail
    }

    override fun initView() {
        setupTabs()
        setupSearch()
    }

    private fun setupTabs() {
        val tabs = listOf(
            TabConfig("In progress") { TripEventTabFragment.newInstance(true) },
            TabConfig("In Completed") { TripEventTabFragment.newInstance(false) }
        )

        binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
        
        // Set default selected tab to "In progress"
        binding.viewPager.setCurrentItem(0, false)
    }

    private fun setupSearch() {
        binding.buttonSearch.setOnClickListener {
            // TODO: Implement search functionality
        }
    }
}

@AndroidEntryPoint
class TripEventTabFragment : BaseFragment<com.example.statistics.databinding.FragmentTripEventTabBinding>(
    com.example.statistics.databinding.FragmentTripEventTabBinding::inflate
) {
    private val viewModel: TripEventViewModel by viewModels()
    private val adapter = TripEventAdapter { tripEventData ->
        // TODO: Navigate to event detail
    }
    
    private var isActive: Boolean = true

    override fun initView() {
        val isActiveArg = arguments?.getBoolean(ARG_IS_ACTIVE, true) ?: true
        isActive = isActiveArg
        
        setupRecyclerView()
        viewModel.loadTripEvents(isActive)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@TripEventTabFragment.adapter
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Idle -> {}
                is UIState.Loading -> {}
                is UIState.Success -> {
                    if (state.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        showEventList()
                        adapter.submitList(state.data)
                    }
                }
                is UIState.Error -> {
                    showEmptyView()
                }
            }
        }
    }

    private fun showEmptyView() {
        binding.layoutEmpty.visibility = android.view.View.VISIBLE
        binding.recyclerViewEvents.visibility = android.view.View.GONE
    }

    private fun showEventList() {
        binding.layoutEmpty.visibility = android.view.View.GONE
        binding.recyclerViewEvents.visibility = android.view.View.VISIBLE
    }

    companion object {
        private const val ARG_IS_ACTIVE = "is_active"

        fun newInstance(isActive: Boolean): TripEventTabFragment {
            return TripEventTabFragment().apply {
                arguments = android.os.Bundle().apply {
                    putBoolean(ARG_IS_ACTIVE, isActive)
                }
            }
        }
    }
}

