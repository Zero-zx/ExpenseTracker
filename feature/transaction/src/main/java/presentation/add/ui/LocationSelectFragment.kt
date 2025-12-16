package presentation.add.ui

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import base.BaseFragment
import base.UIState
import com.example.common.R as CommonR
import com.example.transaction.R as TransactionR
import com.example.transaction.databinding.FragmentLocationSelectBinding
import constants.FragmentResultKeys.REQUEST_SELECT_LOCATION_ID
import constants.FragmentResultKeys.RESULT_LOCATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import presentation.add.adapter.LocationAdapter
import presentation.add.viewModel.AddTransactionViewModel
import presentation.add.viewModel.LocationSelectViewModel
import transaction.model.Location
import ui.navigateBack
import ui.setSelectionResult

@AndroidEntryPoint
class LocationSelectFragment : BaseFragment<FragmentLocationSelectBinding>(
    FragmentLocationSelectBinding::inflate
) {
    private val viewModel: LocationSelectViewModel by viewModels()
    private val addTransactionViewModel: AddTransactionViewModel by hiltNavGraphViewModels(TransactionR.id.transaction_nav_graph)
    private lateinit var adapter: LocationAdapter
    private val selectedLocationId: Long by lazy {
        parentFragment?.arguments?.getLong("selected_location_id", -1L) ?: -1L
    }

    // Track current search query to filter temporary locations
    private val currentSearchQuery = MutableStateFlow<String>("")

    override fun initView() {
        adapter = LocationAdapter(
            { location ->
                onLocationSelected(location.id)
            },
            {
                // TODO: Handle item update
            }
        )
        binding.recyclerView.adapter = adapter

        // Set up search
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.textViewAddress.text = customAddressText(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                currentSearchQuery.value = query
                if (query.isBlank()) {
                    viewModel.loadLocations()
                } else {
                    viewModel.searchLocations(query)
                }
            }
        })
    }

    override fun initListener() {
        binding.apply {
            buttonBack.setOnClickListener {
                navigateBack()
            }

            buttonAddLocation.setOnClickListener {
                showAddLocationView()
            }

            buttonSave.setOnClickListener {
                val locationName = editTextSearch.text.toString()
                if (locationName.isBlank()) {
                    return@setOnClickListener
                }

                // Create temporary location and add to AddTransactionViewModel
                val location = Location(
                    id = 0, // Will be assigned temporary ID
                    name = locationName,
                    accountId = addTransactionViewModel.getCurrentAccountId() ?: 1L
                )
                addTransactionViewModel.addTemporaryLocation(location)

                // Clear input and show list
                editTextSearch.text?.clear()
                showRecyclerView()
            }
        }
    }

    override fun observeData() {
        // Observe persisted locations from LocationSelectViewModel and merge with temporary ones
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.uiState,
                addTransactionViewModel.temporaryLocations,
                currentSearchQuery
            ) { persistedState, temporaryLocations, searchQuery ->
                when (persistedState) {
                    is UIState.Success -> {
                        // Filter temporary locations by search query if searching
                        val filteredTemporary = if (searchQuery.isNotBlank()) {
                            temporaryLocations.filter {
                                it.name.contains(searchQuery, ignoreCase = true)
                            }
                        } else {
                            temporaryLocations
                        }
                        // Merge persisted and temporary locations (temporary first)
                        val mergedLocations = filteredTemporary + persistedState.data
                        UIState.Success(mergedLocations)
                    }

                    is UIState.Loading -> UIState.Loading
                    is UIState.Error -> persistedState
                    else -> UIState.Idle
                }
            }.collect { mergedState ->
                when (mergedState) {
                    is UIState.Loading -> {}
                    is UIState.Success -> {
                        if (mergedState.data.isNotEmpty()) {
                            showRecyclerView()
                            adapter.submitList(mergedState.data)
                            if (selectedLocationId != -1L) {
                                val selectedLocation =
                                    mergedState.data.find { it.id == selectedLocationId }
                                adapter.setSelectedLocation(selectedLocation)
                            }
                        } else {
                            showEmptyView()
                        }
                    }

                    else -> {
                        showEmptyView()
                    }
                }
            }
        }
    }

    private fun onLocationSelected(locationId: Long) {
        setSelectionResult(
            REQUEST_SELECT_LOCATION_ID,
            bundleOf(RESULT_LOCATION_ID to locationId)
        )
        navigateBack()
    }

    fun showEmptyView() {
        binding.layoutEmpty.isVisible = true
        binding.recyclerView.isVisible = false
    }

    fun showRecyclerView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = true
    }

    fun showAddLocationView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = false
    }

    fun customAddressText(address: String): SpannableStringBuilder {
        var prefix = "Set"
        val suffix = " as address"
        if (address.isNotBlank()) {
            prefix += " "
        }
        val startIndex = prefix.length
        val spannable = SpannableStringBuilder().apply {
            append(prefix)
            append(address)
            append(suffix)
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        CommonR.color.blue_bg
                    )
                ),
                startIndex,
                startIndex + address.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }
}

