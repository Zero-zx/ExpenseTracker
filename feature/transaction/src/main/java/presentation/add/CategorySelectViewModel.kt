package presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import presentation.CategoryUiState
import javax.inject.Inject

@HiltViewModel
class CategorySelectViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _categoryState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryState = _categoryState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onStart { _categoryState.value = CategoryUiState.Loading }
                .catch { exception ->
                    _categoryState.value = CategoryUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { categories ->
                    _categoryState.value = CategoryUiState.Success(categories)
                }
        }
    }
}

