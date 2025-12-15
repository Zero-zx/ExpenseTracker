package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import transaction.model.Category
import transaction.model.CategoryType
import transaction.usecase.GetCategoriesByTypeUseCase
import javax.inject.Inject

@HiltViewModel
class CategoryTabViewModel @Inject constructor(
    private val getCategoriesByTypeUseCase: GetCategoriesByTypeUseCase
) : BaseViewModel<List<Category>>() {

    fun loadCategories(categoryType: CategoryType) {
        viewModelScope.launch {
            getCategoriesByTypeUseCase(categoryType)
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

