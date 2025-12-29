package presentation.detail

import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import category.model.Category
import category.model.CategoryType
import com.example.statistics.databinding.FragmentCategoryMultiSelectBinding
import constants.FragmentResultKeys.REQUEST_SELECT_CATEGORY_IDS
import constants.FragmentResultKeys.RESULT_CATEGORY_IDS
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.ExpandableCategoryMultiSelectAdapter
import ui.navigateBack
import ui.setSelectionResult

@AndroidEntryPoint
class CategoryMultiSelectFragment : BaseFragment<FragmentCategoryMultiSelectBinding>(
    FragmentCategoryMultiSelectBinding::inflate
) {
    private val viewModel: CategoryMultiSelectViewModel by viewModels()
    private val selectedCategoryIds: MutableSet<Long> = mutableSetOf()
    private lateinit var adapter: ExpandableCategoryMultiSelectAdapter
    private var allCategories: List<Category> = emptyList()

    private val categoryType: CategoryType by lazy {
        arguments?.getString(ARG_CATEGORY_TYPE)?.let { typeString ->
            CategoryType.valueOf(typeString)
        } ?: CategoryType.EXPENSE
    }

    override fun initView() {
        // Get initially selected category IDs from arguments
        // - null or not set: default to "select all" (will be set after categories are loaded)
        // - empty array: deselect all (keep selectedCategoryIds empty)
        // - array with -1L as first element: default/select all (will be set after categories are loaded)
        // - array with IDs: use those IDs
        val initialIds = arguments?.getLongArray(ARG_SELECTED_CATEGORY_IDS)
        when {
            initialIds == null -> {
                // Not set = default = select all (will be set after categories are loaded)
            }
            initialIds.isEmpty() -> {
                // Empty array = deselect all (keep selectedCategoryIds empty)
                selectedCategoryIds.clear()
            }
            initialIds.size == 1 && initialIds[0] == -1L -> {
                // Special marker for "default/select all" (will be set after categories are loaded)
            }
            else -> {
                // Has IDs, use them
                selectedCategoryIds.clear()
                selectedCategoryIds.addAll(initialIds.toList())
            }
        }

        // Initialize adapter
        adapter = ExpandableCategoryMultiSelectAdapter(
            onCategoryToggle = { categoryId ->
                val category = allCategories.find { it.id == categoryId }
                val itemsToNotify = mutableListOf<Long>(categoryId)
                
                if (category != null && category.parentId != null) {
                    // This is a child category
                    if (selectedCategoryIds.contains(categoryId)) {
                        selectedCategoryIds.remove(categoryId)
                    } else {
                        selectedCategoryIds.add(categoryId)
                    }
                    // Update parent state: if all children are selected, select parent; otherwise deselect parent
                    val parentId = category.parentId
                    if (parentId != null) {
                        itemsToNotify.add(parentId)
                        val childIds = allCategories.filter { it.parentId == parentId }.map { it.id }
                        val allChildrenSelected = childIds.all { selectedCategoryIds.contains(it) }
                        if (allChildrenSelected) {
                            selectedCategoryIds.add(parentId)
                        } else {
                            selectedCategoryIds.remove(parentId)
                        }
                    }
                } else {
                    // This is a parent category or orphan category
                    val isParentSelected = selectedCategoryIds.contains(categoryId)
                    val childIds = allCategories.filter { it.parentId == categoryId }.map { it.id }
                    
                    if (isParentSelected) {
                        // Deselect parent and all children
                        selectedCategoryIds.remove(categoryId)
                        selectedCategoryIds.removeAll(childIds)
                        itemsToNotify.addAll(childIds)
                    } else {
                        // Select parent and all children
                        selectedCategoryIds.add(categoryId)
                        selectedCategoryIds.addAll(childIds)
                        itemsToNotify.addAll(childIds)
                    }
                }
                
                // Only update checkbox state for affected items (most efficient)
                adapter.updateCheckboxState(binding.recyclerViewCategories, itemsToNotify)
                updateSelectAllState()
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
                
                // Only update checkbox state for affected items (most efficient)
                val itemsToNotify = mutableListOf(parentId)
                itemsToNotify.addAll(childIds)
                adapter.updateCheckboxState(binding.recyclerViewCategories, itemsToNotify)
                updateSelectAllState()
            },
            selectedCategoryIds = { selectedCategoryIds }
        )

        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        viewModel.loadCategories(categoryType)
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
                    allCategories = state.data
                    // If no initial selection, default to "select all"
                    if (selectedCategoryIds.isEmpty()) {
                        val allCategoryIds = state.data.map { it.id }
                        selectedCategoryIds.addAll(allCategoryIds)
                    }
                    adapter.submitCategories(state.data)
                    updateSelectAllState()
                }

                is UIState.Error -> {
                    android.util.Log.e(
                        "CategoryMultiSelect",
                        "Error loading categories: ${state.message}"
                    )
                }

                else -> {}
            }
        }

    }

    private fun setupSelectAllListener() {
        binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            val allCategoryIds = allCategories.map { it.id }
            if (isChecked) {
                selectedCategoryIds.clear()
                selectedCategoryIds.addAll(allCategoryIds)
                adapter.refresh()
            } else {
                selectedCategoryIds.clear()
                adapter.refresh()
            }
            updateSelectAllState()
        }
    }

    private fun updateSelectAllState() {
        val allCategoryIds = allCategories.map { it.id }
        val allSelected = allCategoryIds.isNotEmpty() && selectedCategoryIds.containsAll(allCategoryIds)
        binding.checkboxSelectAll.setOnCheckedChangeListener(null)
        binding.checkboxSelectAll.isChecked = allSelected
        setupSelectAllListener()

        val selectedCount = selectedCategoryIds.size
        binding.textViewSelectedCount.text = "$selectedCount categories"
    }

    private fun onConfirmSelection() {
        // If all categories are selected, don't set the key (null = no filter, show all)
        // Otherwise, return the selected IDs
        val allCategoryIds = allCategories.map { it.id }
        val allSelected = selectedCategoryIds.containsAll(allCategoryIds) && 
                         allCategoryIds.isNotEmpty()
        
        val bundle = if (allSelected) {
            // All selected = no filter = show all, don't set the key
            bundleOf()
        } else {
            // Return selected IDs (can be empty array if deselect all)
            bundleOf(RESULT_CATEGORY_IDS to selectedCategoryIds.toLongArray())
        }
        
        setSelectionResult(REQUEST_SELECT_CATEGORY_IDS, bundle)
        navigateBack()
    }

    companion object {
        const val ARG_SELECTED_CATEGORY_IDS = "selected_category_ids"
        const val ARG_CATEGORY_TYPE = "category_type"
    }
}

