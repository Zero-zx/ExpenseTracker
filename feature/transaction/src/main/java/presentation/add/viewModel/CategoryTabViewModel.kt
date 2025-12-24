package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.UIState
import category.model.Category
import category.model.CategoryType
import category.usecase.GetCategoriesByTypeUseCase
import category.usecase.GetMostUsedCategoriesUseCase
import category.usecase.SearchCategoriesByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryTabViewModel @Inject constructor(
    private val getCategoriesByTypeUseCase: GetCategoriesByTypeUseCase,
    private val getMostUsedCategoriesUseCase: GetMostUsedCategoriesUseCase,
    private val searchCategoriesByTypeUseCase: SearchCategoriesByTypeUseCase
) : BaseViewModel<List<Category>>() {

    private val _mostUsedCategories = MutableStateFlow<UIState<List<Category>>>(UIState.Idle)
    val mostUsedCategories: StateFlow<UIState<List<Category>>> = _mostUsedCategories.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

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

    fun loadMostUsedCategories(categoryType: CategoryType) {
        viewModelScope.launch {
            try {
                _mostUsedCategories.value = UIState.Loading
                val mostUsed = getMostUsedCategoriesUseCase(categoryType, limit = 3)
                _mostUsedCategories.value = UIState.Success(mostUsed)
            } catch (e: Exception) {
                _mostUsedCategories.value = UIState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun updateSearchQuery(query: String, categoryType: CategoryType) {
        if (query.isEmpty()) {
            loadCategories(categoryType)
        } else {
            viewModelScope.launch {
                searchCategoriesByTypeUseCase(query, CategoryType.EXPENSE)
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
}



