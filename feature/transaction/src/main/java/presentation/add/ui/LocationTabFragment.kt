package presentation.add.ui

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentLocationTabBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.LocationAdapter
import presentation.add.model.LocationTabType
import presentation.add.viewModel.LocationSelectViewModel
import transaction.model.Location
import ui.CustomAlertDialog
import ui.showEditDialog

@AndroidEntryPoint
class LocationTabFragment : BaseFragment<FragmentLocationTabBinding>(
    FragmentLocationTabBinding::inflate
) {
    private val viewModel: LocationSelectViewModel by viewModels()
    private lateinit var adapter: LocationAdapter
    private var tabType: LocationTabType = LocationTabType.IN_PROGRESS
    private val selectedLocationName: String by lazy {
        arguments?.getString(ARG_SELECTED_LOCATION_NAME, "") ?: ""
    }

    override fun initView() {
        adapter = LocationAdapter(
            onItemClick = { location ->
                (parentFragment as LocationSelectFragment).onLocationSelected(location.name)
            },
            onItemUpdate = { location ->
                handleLocationEdit(location)
            }
        )
        binding.recyclerView.adapter = adapter

        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? LocationTabType
        tabType = tabTypeArg ?: LocationTabType.IN_PROGRESS

        showAddLocationViewWithData(selectedLocationName)
    }

    override fun initListener() {
        binding.buttonAddTrip.setOnClickListener {
            showListView()
        }

        binding.buttonSave.setOnClickListener {
            val locationName = binding.editTextLocationName.text.toString()
            if (locationName.isBlank()) {
                return@setOnClickListener
            }

            (parentFragment as LocationSelectFragment).onLocationSelected(locationName)
        }

        binding.editTextLocationName.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSearchQuery(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    override fun observeData() {
        // Observe location list from database
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> handleSuccessState(state.data)
                is UIState.Error -> handleErrorState()
                is UIState.Idle -> handleEmptyState()
            }
        }
    }

    private fun handleSuccessState(locations: List<Location>) {
        handleLocationListLoaded(locations)
    }

    private fun handleErrorState() {
        showEmptyView()
    }

    private fun handleEmptyState() {
        if (binding.editTextLocationName.text.isNullOrBlank()) showEmptyView()
    }


    private fun handleLocationListLoaded(locations: List<Location>) {
        showListView()
        adapter.submitList(locations)
    }


    private fun handleLocationEdit(location: Location) {
        showEditDialog(
            title = "Edit Location",
            inputHint = "Enter location name",
            name = location.name,
            isCompleted = false,
            onUpdate = { name, _ ->
                viewModel.updateLocation(
                    location.copy(
                        name = name
                    )
                )
            },
            onDelete = {
                showDeleteConfirmation(location)
            }
        )
    }

    private fun showDeleteConfirmation(location: Location) {
        CustomAlertDialog.Builder(requireContext())
            .setTitle("Delete Location")
            .setMessage("Are you sure you want to delete '${location.name}'?")
            .setPositiveButton("Delete") { dialog ->
                viewModel.deleteLocation(location.id)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAddLocationViewWithData(locationName: String) {
        showListView()
        binding.editTextLocationName.setText(locationName)
    }

    fun showEmptyView() {
        binding.layoutEmpty.isVisible = true
        binding.layoutAddLocation.isVisible = false
        binding.buttonAddTrip.isVisible = tabType == LocationTabType.IN_PROGRESS
    }


    fun showListView() {
        binding.layoutEmpty.isVisible = false
        binding.layoutAddLocation.isVisible = true
    }

    companion object {
        private const val ARG_TAB_TYPE = "location_tab_type"
        const val ARG_SELECTED_LOCATION_NAME = "selected_location_name"

        fun newInstance(tabType: LocationTabType): LocationTabFragment {
            return LocationTabFragment().apply {
                arguments = android.os.Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }
}

