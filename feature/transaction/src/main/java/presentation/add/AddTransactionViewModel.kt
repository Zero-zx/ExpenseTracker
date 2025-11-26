package presentation.add

import android.accounts.Account
import android.app.usage.UsageEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _categoryState = MutableStateFlow(CategoryUiState())
    val categoryState = _categoryState.asStateFlow()

    private val _transactionSate = MutableStateFlow(AddTransactionUiState())
    val transactionSate = _transactionSate.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onStart { _categoryState.value = CategoryUiState(isLoading = true) }
                .catch {
                    _categoryState.value = CategoryUiState(error = it.message)
                }
                .collect { categories ->
                    _categoryState.value = CategoryUiState(categories = categories)
                }
        }
    }

    fun addTransaction(
        amount: Double,
        description: String?,
        category: Category,
        account: Account,
        event: UsageEvents.Event? = null,
        partner: Nothing? = null
    ) {
        viewModelScope.launch {
            _transactionSate.value = _transactionSate.value.copy(isLoading = true)
            try {
                val id = addTransactionUseCase(
                    amount = amount,
                    category = _transactionSate.value.selectedCategory ?: return@launch,
                    description = description
                )
                _transactionSate.value = _transactionSate.value.copy(transactionId = id)
            } catch (e: Exception) {
                _transactionSate.value = _transactionSate.value.copy(error = e.message)
            }
        }
    }

    fun selectCategory(category: Category) {
        _transactionSate.update { it.copy(selectedCategory = category) }
    }

    fun onHistoryClick() {
        navigator.navigateToTransaction()
    }

    fun resetTransactionState() {
        _transactionSate.value = AddTransactionUiState()
    }
}