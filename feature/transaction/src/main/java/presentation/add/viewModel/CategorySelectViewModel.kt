package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import transaction.model.Category
import transaction.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategorySelectViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel<List<Category>>() {

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
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

