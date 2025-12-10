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
import base.BaseFragment
import base.UIState
import com.example.common.R
import com.example.transaction.databinding.FragmentLocationSelectBinding
import constants.FragmentResultKeys.REQUEST_SELECT_LOCATION_ID
import constants.FragmentResultKeys.RESULT_LOCATION_ID
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.LocationAdapter
import presentation.add.viewModel.LocationSelectViewModel
import ui.navigateBack
import ui.setSelectionResult

@AndroidEntryPoint
class LocationSelectFragment : BaseFragment<FragmentLocationSelectBinding>(
    FragmentLocationSelectBinding::inflate
) {
    private val viewModel: LocationSelectViewModel by viewModels()
    private lateinit var adapter: LocationAdapter
    private val selectedLocationId: Long by lazy {
        parentFragment?.arguments?.getLong("selected_location_id", -1L) ?: -1L
    }

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
                viewModel.searchLocations(s?.toString() ?: "")
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
                viewModel.addLocation(locationName)
            }
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {

                    if (state.data.isNotEmpty()) {
                        showRecyclerView()
                        adapter.submitList(state.data)
                        if (selectedLocationId != -1L) {
                            val selectedLocation = state.data.find { it.id == selectedLocationId }
                            adapter.setSelectedLocation(selectedLocation)
                        }
                    }
                }

                else -> {
                    showEmptyView()
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
                        R.color.blue_bg
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

