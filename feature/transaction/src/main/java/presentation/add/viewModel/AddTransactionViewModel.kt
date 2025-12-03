package presentation.add.viewModel

import account.model.Account
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import navigation.Navigator
import presentation.CategoryUiState
import transaction.model.Category
import transaction.model.Event
import transaction.usecase.GetCategoriesUseCase
import usecase.AddTransactionUseCase
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

    private val _selectedEvents = MutableStateFlow<List<Event>>(emptyList())
    val selectedEvents = _selectedEvents.asStateFlow()


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
        description: String?,
        createAt: Long
    ) {
        viewModelScope.launch {
            val selectedCategory = _selectedCategory.value ?: return@launch
            val selectedAccount = _selectedAccount.value ?: return@launch
            val selectedEvent = _selectedEvents.value.firstOrNull()

            setLoading()
            try {
                val id = addTransactionUseCase(
                    amount = amount,
                    category = selectedCategory,
                    account = selectedAccount,
                    event = selectedEvent,
                    description = description,
                    createAt = createAt
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

    fun selectEvent(event: Event) {
        val currentEvents = _selectedEvents.value.toMutableList()
        if (currentEvents.any { it.id == event.id }) {
            currentEvents.removeAll { it.id == event.id }
        } else {
            currentEvents.add(event)
        }
        _selectedEvents.value = currentEvents
    }

    fun removeEvent(event: Event) {
        val currentEvents = _selectedEvents.value.toMutableList()
        currentEvents.removeAll { it.id == event.id }
        _selectedEvents.value = currentEvents
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

    fun toSelectEvent() {
        navigator.navigateToSelectEvent()
    }

    fun navigateBack() {
        navigator.popBackStack()
    }

    fun resetTransactionState() {
        resetState()
    }
}