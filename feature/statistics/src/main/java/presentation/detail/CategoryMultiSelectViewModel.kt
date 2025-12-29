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
import category.usecase.GetReportCategoriesByTypeUseCase
import javax.inject.Inject

@HiltViewModel
class CategoryMultiSelectViewModel @Inject constructor(
    private val getReportCategoriesByTypeUseCase: GetReportCategoriesByTypeUseCase
) : BaseViewModel<List<Category>>() {

    fun loadCategories(categoryType: CategoryType) {
        viewModelScope.launch {
            getReportCategoriesByTypeUseCase(categoryType)
                .onStart { setLoading() }
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { categories ->
                    setSuccess(categories)
                }
        }
    }
}

