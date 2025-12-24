package presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import category.model.Category
import category.model.CategoryType
import category.usecase.GetCategoriesByTypeUseCase
import javax.inject.Inject

@HiltViewModel
class CategoryMultiSelectViewModel @Inject constructor(
    private val getCategoriesByTypeUseCase: GetCategoriesByTypeUseCase
) : BaseViewModel<List<Category>>() {

    private val _allCategoryIds = MutableLiveData<List<Long>>()
    val allCategoryIds: LiveData<List<Long>> = _allCategoryIds

    fun loadCategories(categoryType: CategoryType) {
        viewModelScope.launch {
            getCategoriesByTypeUseCase(categoryType)
                .onStart { setLoading() }
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { categories ->
                    setSuccess(categories)
                    _allCategoryIds.value = categories.map { it.id }
                }
        }
    }

    fun getAllCategoryIdsWithChildren(categoryType: CategoryType) {
        viewModelScope.launch {
            getCategoriesByTypeUseCase(categoryType)
                .collect { categories ->
                    // Update all category IDs including both parent and child
                    _allCategoryIds.value = categories.map { it.id }
                }
        }
    }
}

