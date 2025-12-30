package presentation.detail.ui

import android.util.Log
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import category.model.CategoryType
import com.example.statistics.R
import com.example.statistics.databinding.FragmentCategoryMultiSelectBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.ExpandableCategoryMultiSelectAdapter
import presentation.detail.model.SelectableCategory
import presentation.detail.viewmodel.CategoryMultiSelectViewModel
import presentation.detail.viewmodel.SharedViewModel
import ui.navigateBack

@AndroidEntryPoint
class CategoryMultiSelectFragment : BaseFragment<FragmentCategoryMultiSelectBinding>(
    FragmentCategoryMultiSelectBinding::inflate
) {
    private val viewModel: CategoryMultiSelectViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by hiltNavGraphViewModels(R.id.statistics_nav_graph)
    private lateinit var adapter: ExpandableCategoryMultiSelectAdapter
    private var allCategories: List<SelectableCategory> = emptyList()

    private val categoryType: CategoryType by lazy {
        arguments?.getString(ARG_CATEGORY_TYPE)?.let { typeString ->
            CategoryType.valueOf(typeString)
        } ?: CategoryType.EXPENSE
    }

    override fun initView() {
        adapter = ExpandableCategoryMultiSelectAdapter(
            onCategoryClick = { category ->
                viewModel.toggleCategory(category.id)
            }
        )

        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(requireContext())

    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            navigateBack()
        }

        binding.buttonConfirm.setOnClickListener {
            onConfirmSelection()
        }

        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
//            adapter.filter(text?.toString() ?: "")
        }

        setupSelectAllListener()
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {
                    // Show loading if needed
                }

                is UIState.Success -> {
                    allCategories = state.data
                    adapter.submitCategories(state.data)
                    updateSelectAllState()
                }

                is UIState.Error -> {
                    Log.e(
                        "CategoryMultiSelect",
                        "Error loading categories: ${state.message}"
                    )
                }

                else -> {}
            }
        }

        collectState(sharedViewModel.selectedIds) { selectedIds ->
            viewModel.loadCategories(categoryType, selectedIds)
        }

        // Observe categories changes
        collectState(viewModel.categories) { categories ->
            if (categories.isNotEmpty()) {
                allCategories = categories
                adapter.submitCategories(categories)
                updateSelectAllState()
            }
        }
    }

    private fun setupSelectAllListener() {
        binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectAll()
            } else {
                viewModel.deselectAll()
            }
        }
    }

    private fun updateSelectAllState() {
        val selectedCount = allCategories.count { it.isSelected }
        val allSelected = allCategories.isNotEmpty() &&
                selectedCount == allCategories.size

        binding.checkboxSelectAll.setOnCheckedChangeListener(null)
        binding.checkboxSelectAll.isChecked = allSelected
        setupSelectAllListener()

        binding.textViewSelectedCount.text = "$selectedCount categories"
    }

    private fun onConfirmSelection() {
        val selectedIds = viewModel.getSelectedCategoryIds()
        sharedViewModel.updateSelectedIds(selectedIds)
        navigateBack()
    }

    companion object {
        const val ARG_CATEGORY_TYPE = "category_type"
    }
}
