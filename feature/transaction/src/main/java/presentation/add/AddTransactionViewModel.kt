package presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transaction.R
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Category
import domain.usecase.AddTransactionUseCase
import domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import navigation.Navigator
import presentation.AddTransactionUiState
import presentation.CategoryUiState
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val navigator: Navigator,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _categoryState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryState = _categoryState.asStateFlow()

    private val _transactionState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Initial())
    val transactionState = _transactionState.asStateFlow()

    init {
        loadCategories()
    }

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

    fun addTransaction(
        amount: Double,
        description: String?
    ) {
        viewModelScope.launch {
            val selectedCategory = _transactionState.value.selectedCategory ?: return@launch

            _transactionState.value = AddTransactionUiState.Loading(selectedCategory)
            try {
                val id = addTransactionUseCase(
                    amount = amount,
                    category = selectedCategory,
                    description = description
                )
                _transactionState.value = AddTransactionUiState.Success(
                    transactionId = id,
                    selectedCategory = selectedCategory
                )
            } catch (e: Exception) {
                _transactionState.value = AddTransactionUiState.Error(
                    message = e.message ?: "Unknown error occurred",
                    selectedCategory = selectedCategory
                )
            }
        }
    }

    fun selectCategory(category: Category) {
        _transactionState.update { currentState ->
            currentState.withSelectedCategory(category)
        }
    }

    fun onHistoryClick() {
        navigator.navigateToTransaction()
    }

    fun onMoreCategory() {
        navigator.navigateToMoreCategory()
    }

    fun resetTransactionState() {
        val selectedCategory = _transactionState.value.selectedCategory
        _transactionState.value = AddTransactionUiState.Initial(selectedCategory)
    }
}