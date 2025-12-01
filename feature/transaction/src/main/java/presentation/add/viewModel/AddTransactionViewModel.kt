package presentation.add.viewModel

import account.model.Account
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import transaction.model.Category
import transaction.usecase.AddTransactionUseCase
import transaction.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import navigation.Navigator
import presentation.CategoryUiState
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val navigator: Navigator,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel<Long>() {

    private val _categoryState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categoryState = _categoryState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount = _selectedAccount.asStateFlow()


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
            val selectedCategory = _selectedCategory.value ?: return@launch
            setLoading()
            try {
                val id = addTransactionUseCase(
                    amount = amount,
                    category = selectedCategory,
                    description = description
                )
                setSuccess(id)
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    // Allow other fragments to set the selected account in the shared nav-graph scoped ViewModel
    fun selectAccount(account: Account) {
        _selectedAccount.value = account
    }

    fun onHistoryClick() {
        navigator.navigateToTransaction()
    }

    fun toSelectCategory() {
        navigator.navigateToMoreCategory()
    }

    fun toSelectAccount() {
        navigator.navigateToSelectAccount()
    }

    fun navigateBack() {
        navigator.popBackStack()
    }

    fun resetTransactionState() {
        resetState()
    }
}