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

    // Track current search query to filter locations
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

                // Create and persist location immediately, then select it
                val accountId = addTransactionViewModel.getCurrentAccountId() ?: 1L
                val location = Location(
                    name = locationName,
                    accountId = accountId
                )
                addTransactionViewModel.addTemporaryLocation(location)

                // Reload locations to show the newly persisted one
                viewModel.loadLocations()
                
                // Clear input and show list
                editTextSearch.text?.clear()
                showRecyclerView()
            }
        }
    }

    override fun observeData() {
        // Observe persisted locations from LocationSelectViewModel
        // Temporary locations are now persisted immediately, so they appear in the persisted list
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UIState.Loading -> {}
                    is UIState.Success -> {
                        // Filter by search query if searching
                        val filteredLocations = if (currentSearchQuery.value.isNotBlank()) {
                            state.data.filter {
                                it.name.contains(currentSearchQuery.value, ignoreCase = true)
                            }
                        } else {
                            state.data
                        }
                        
                        if (filteredLocations.isNotEmpty()) {
                            showRecyclerView()
                            adapter.submitList(filteredLocations)
                            if (selectedLocationId != -1L) {
                                val selectedLocation =
                                    filteredLocations.find { it.id == selectedLocationId }
                                adapter.setSelectedLocation(selectedLocation)
                            }
                        } else {
                            showEmptyView()
                        }
                    }
                    is UIState.Error -> {
                        showEmptyView()
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

