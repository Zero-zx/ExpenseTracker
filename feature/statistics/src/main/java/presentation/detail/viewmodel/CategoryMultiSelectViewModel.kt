package presentation.detail.viewmodel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import category.model.CategoryType
import category.usecase.GetCategoriesUsedByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import presentation.detail.model.SelectableCategory
import presentation.detail.model.toSelectable
import javax.inject.Inject

@HiltViewModel
class CategoryMultiSelectViewModel @Inject constructor(
    private val getCategoriesUsedByTypeUseCase: GetCategoriesUsedByTypeUseCase
) : BaseViewModel<List<SelectableCategory>>() {

    private val _categories = MutableStateFlow<List<SelectableCategory>>(emptyList())
    val categories: StateFlow<List<SelectableCategory>> = _categories

    fun loadCategories(categoryType: CategoryType, initialSelectedIds: List<Long> = emptyList()) {
        viewModelScope.launch {
            setLoading()
            val categories = getCategoriesUsedByTypeUseCase(categoryType)

            if (categories.isEmpty()) {
                setError("No category available")
                return@launch
            }

            val selectedIds = initialSelectedIds.ifEmpty {
                categories.map { it.id }
            }

            val selectableCategories = categories.map { category ->
                category.toSelectable(isSelected = selectedIds.contains(category.id))
            }

            _categories.value = selectableCategories
            setSuccess(selectableCategories)
        }
    }

    fun toggleCategory(categoryId: Long) {
        val currentCategories = _categories.value
        val category = currentCategories.find { it.id == categoryId } ?: return

        val updatedCategories = if (category.parentId != null) {
            // Child category toggled
            toggleChildCategory(currentCategories, categoryId, category.parentId)
        } else {
            // Parent category toggled
            toggleParentCategory(currentCategories, categoryId)
        }

        _categories.value = updatedCategories
        setSuccess(updatedCategories)
    }

    private fun toggleChildCategory(
        categories: List<SelectableCategory>,
        childId: Long,
        parentId: Long
    ): List<SelectableCategory> {
        return categories.map { category ->
            when (category.id) {
                childId -> {
                    // Toggle the child
                    category.copy(isSelected = !category.isSelected)
                }

                parentId -> {
                    // Update parent based on all children's state
                    val siblings = categories.filter { it.parentId == parentId }
                    val childToToggle = siblings.find { it.id == childId }
                    val otherChildren = siblings.filter { it.id != childId }

                    val allChildrenSelected = otherChildren.all { it.isSelected } &&
                            childToToggle?.isSelected == false

                    category.copy(isSelected = allChildrenSelected)
                }

                else -> category
            }
        }
    }

    private fun toggleParentCategory(
        categories: List<SelectableCategory>,
        parentId: Long
    ): List<SelectableCategory> {
        val parent = categories.find { it.id == parentId } ?: return categories
        val newParentState = !parent.isSelected

        return categories.map { category ->
            when {
                category.id == parentId -> {
                    // Toggle parent
                    category.copy(isSelected = newParentState)
                }

                category.parentId == parentId -> {
                    // Toggle all children to match parent
                    category.copy(isSelected = newParentState)
                }

                else -> category
            }
        }
    }

    fun selectAll() {
        val updatedCategories = _categories.value.map { it.copy(isSelected = true) }
        _categories.value = updatedCategories
        setSuccess(updatedCategories)
    }

    fun deselectAll() {
        val updatedCategories = _categories.value.map { it.copy(isSelected = false) }
        _categories.value = updatedCategories
        setSuccess(updatedCategories)
        setError("No category selected")
    }

    fun getSelectedCategoryIds(): List<Long> {
        return _categories.value.filter { it.isSelected }.map { it.id }
    }
}
