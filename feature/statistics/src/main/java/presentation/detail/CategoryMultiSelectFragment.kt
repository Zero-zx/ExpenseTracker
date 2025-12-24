package presentation.detail

import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentCategoryMultiSelectBinding
import constants.FragmentResultKeys.REQUEST_SELECT_CATEGORY_IDS
import constants.FragmentResultKeys.RESULT_CATEGORY_IDS
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.ExpandableCategoryMultiSelectAdapter
import category.model.CategoryType
import ui.navigateBack
import ui.setSelectionResult

@AndroidEntryPoint
class CategoryMultiSelectFragment : BaseFragment<FragmentCategoryMultiSelectBinding>(
    FragmentCategoryMultiSelectBinding::inflate
) {
    private val viewModel: CategoryMultiSelectViewModel by viewModels()
    private val selectedCategoryIds: MutableSet<Long> = mutableSetOf()
    
    private lateinit var adapter: ExpandableCategoryMultiSelectAdapter
    private var allCategoryIds: List<Long> = emptyList()

    private var categoryType: CategoryType = CategoryType.EXPENSE

    override fun initView() {
        // Get initially selected category IDs from arguments
        arguments?.getLongArray("selected_category_ids")?.let {
            selectedCategoryIds.addAll(it.toList())
        }

        // Get category type from arguments
        arguments?.getString("category_type")?.let { typeString ->
            categoryType = CategoryType.valueOf(typeString)
        }

        // Initialize adapter
        adapter = ExpandableCategoryMultiSelectAdapter(
            onCategoryToggle = { categoryId ->
                if (selectedCategoryIds.contains(categoryId)) {
                    selectedCategoryIds.remove(categoryId)
                } else {
                    selectedCategoryIds.add(categoryId)
                }
                refreshAdapter()
                updateSelectAllState(allCategoryIds)
            },
            onParentCategoryToggle = { parentId, childIds ->
                val isParentSelected = selectedCategoryIds.contains(parentId)
                if (isParentSelected) {
                    // Deselect parent and all children
                    selectedCategoryIds.remove(parentId)
                    selectedCategoryIds.removeAll(childIds)
                } else {
                    // Select parent and all children
                    selectedCategoryIds.add(parentId)
                    selectedCategoryIds.addAll(childIds)
                }
                refreshAdapter()
                updateSelectAllState(allCategoryIds)
            },
            selectedCategoryIds = { selectedCategoryIds }
        )

        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        
        // Setup select all listener will be done in observeData after categories are loaded

        viewModel.loadCategories(categoryType)
    }

    private fun refreshAdapter() {
        // Refresh adapter by submitting categories again
        viewModel.uiState.value?.let { state ->
            if (state is UIState.Success) {
                adapter.submitCategories(state.data)
            }
        }
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            navigateBack()
        }

        binding.buttonConfirm.setOnClickListener {
            onConfirmSelection()
        }

        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            adapter.filter(text?.toString() ?: "")
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {
                    // Show loading if needed
                }
                is UIState.Success -> {
                    if (state.data.isNotEmpty()) {
                        adapter.submitCategories(state.data)
                        // Update select all state will be handled by allCategoryIds observer
                    } else {
                        // Show empty state if needed
                        android.util.Log.d("CategoryMultiSelect", "No categories found")
                    }
                }
                is UIState.Error -> {
                    android.util.Log.e("CategoryMultiSelect", "Error loading categories: ${state.message}")
                }
                else -> {}
            }
        }

        // Observe all category IDs for select all functionality
        viewModel.allCategoryIds.observe(viewLifecycleOwner) { allIds ->
            allCategoryIds = allIds
            if (allIds.isNotEmpty()) {
                setupSelectAllListener(allIds)
                updateSelectAllState(allIds)
            }
        }
    }

    private fun setupSelectAllListener(allIds: List<Long>) {
        binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Select all categories including parent and child
                selectedCategoryIds.clear()
                selectedCategoryIds.addAll(allIds)
                refreshAdapter()
                updateSelectAllState(allIds)
            } else {
                // Deselect all
                selectedCategoryIds.clear()
                refreshAdapter()
                updateSelectAllState(allIds)
            }
        }
    }

    private fun updateSelectAllState(allIds: List<Long>) {
        val allSelected = allIds.isNotEmpty() && selectedCategoryIds.containsAll(allIds)
        binding.checkboxSelectAll.setOnCheckedChangeListener(null)
        binding.checkboxSelectAll.isChecked = allSelected
        setupSelectAllListener(allIds)
        
        val selectedCount = selectedCategoryIds.size
        binding.textViewSelectedCount.text = "$selectedCount categories"
    }

    private fun onConfirmSelection() {
        setSelectionResult(
            REQUEST_SELECT_CATEGORY_IDS,
            bundleOf(RESULT_CATEGORY_IDS to selectedCategoryIds.toLongArray())
        )
        navigateBack()
    }

    companion object {
        const val ARG_SELECTED_CATEGORY_IDS = "selected_category_ids"
        const val ARG_CATEGORY_TYPE = "category_type"

        fun newInstance(
            categoryType: CategoryType,
            selectedCategoryIds: LongArray? = null
        ): CategoryMultiSelectFragment {
            return CategoryMultiSelectFragment().apply {
                arguments = android.os.Bundle().apply {
                    putString(ARG_CATEGORY_TYPE, categoryType.name)
                    selectedCategoryIds?.let {
                        putLongArray(ARG_SELECTED_CATEGORY_IDS, it)
                    }
                }
            }
        }
    }
}

