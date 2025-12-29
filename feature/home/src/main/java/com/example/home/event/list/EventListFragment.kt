package com.example.home.event.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import com.example.home.databinding.FragmentEventListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventListFragment : BaseFragment<FragmentEventListBinding>(
    FragmentEventListBinding::inflate
) {

    private val viewModel: EventListViewModel by viewModels()
    private val adapter = EventListAdapter(
        onItemClick = { event ->
            // TODO: Navigate to event detail if needed
            Toast.makeText(context, "Clicked: ${event.eventName}", Toast.LENGTH_SHORT).show()
        }
    )

    override fun initView() {
        binding.recyclerViewEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@EventListFragment.adapter
        }
    }

    override fun initListener() {
        binding.fabAddEvent.setOnClickListener {
            viewModel.navigateToAddEvent()
        }

        binding.buttonBack.setOnClickListener {
            viewModel.navigateBack()
        }
    }

    override fun observeData() {
        // Load events for account ID 1 (you can get this from shared preferences or arguments)
        viewModel.loadEvents(1L)

        collectState(viewModel.uiState) { state ->
            when (state) {
                is EventListUiState.Loading -> showLoading()
                is EventListUiState.Empty -> showEmpty()
                is EventListUiState.Success -> showEvents(state.events)
                is EventListUiState.Error -> showError(state.message)
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerViewEvents.visibility = View.GONE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewEvents.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
            layoutError.visibility = View.GONE
        }
    }

    private fun showEvents(events: List<transaction.model.Event>) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewEvents.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE
        }
        adapter.submitList(events)
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewEvents.visibility = View.GONE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.VISIBLE
            textViewError.text = message
        }
    }
}

