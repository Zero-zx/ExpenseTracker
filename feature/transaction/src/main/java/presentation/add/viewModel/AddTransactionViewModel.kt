package presentation.add.viewModel

import account.model.Account
import account.usecase.GetAccountByIdUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import navigation.Navigator
import transaction.model.Category
import transaction.model.Event
import transaction.model.Location
import transaction.model.PayeeTransaction
import transaction.usecase.GetCategoriesUseCase
import transaction.usecase.GetEventByIdUseCase
import transaction.usecase.GetLocationByIdUseCase
import transaction.usecase.GetPayeeByIdUseCase
import usecase.AddTransactionUseCase
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val navigator: Navigator,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val getPayeeByIdUseCase: GetPayeeByIdUseCase
) : BaseViewModel<Long>() {

    private val _categoryState = MutableStateFlow<UIState<List<Category>>>(UIState.Loading)
    val categoryState = _categoryState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount = _selectedAccount.asStateFlow()

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent = _selectedEvent.asStateFlow()

    private val _selectedPayees = MutableStateFlow<List<PayeeTransaction>>(emptyList())
    val selectedPayees = _selectedPayees.asStateFlow()

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()


    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onStart { _categoryState.value = UIState.Loading }
                .catch { exception ->
                    _categoryState.value = UIState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { categories ->
                    _categoryState.value = UIState.Success(categories)
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
            val selectedEvent = _selectedEvent.value ?: return@launch

            setLoading()
            try {
                val payeeIds = _selectedPayees.value.map { it.id }
                val id = addTransactionUseCase(
                    amount = amount,
                    category = selectedCategory,
                    account = selectedAccount,
                    event = selectedEvent,
                    description = description,
                    createAt = createAt,
                    location = _selectedLocation.value,
                    payeeIds = payeeIds
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

    fun selectCategoryById(categoryId: Long) {
        val currentState = categoryState.value
        if (currentState is UIState.Success) {
            currentState.data.find { it.id == categoryId }?.let { category ->
                _selectedCategory.value = category
            }
        }
    }

    fun selectAccountById(accountId: Long) {
        viewModelScope.launch {
            try {
                val account = withContext(Dispatchers.IO) {
                    getAccountByIdUseCase(accountId)
                }
                if (account != null) {
                    _selectedAccount.value = account
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun selectEventById(eventId: Long) {
        viewModelScope.launch {
            try {
                val event = withContext(Dispatchers.IO) {
                    getEventByIdUseCase(eventId)
                }
                if (event != null) {
                    _selectedEvent.value = event
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun removeEvent(event: Event) {
        _selectedEvent.value = null
    }

    fun onHistoryClick() {
        navigator.navigateToTransaction()
    }

    fun toSelectCategory() {
        navigator.navigateToMoreCategory()
    }

    fun toSelectAccount() {
        navigator.navigateToSelectAccount(selectedAccount.value?.id ?: -1L)
    }

    fun toSelectEvent() {
        navigator.navigateToSelectEvent(selectedEvent.value?.id ?: -1L)
    }

    fun toSelectPayee() {
        val selectedPayeeIds = _selectedPayees.value.map { it.id }.toLongArray()
        navigator.navigateToSelectPayee(selectedPayeeIds)
    }

    fun toSelectLocation() {
        navigator.navigateToSelectLocation(_selectedLocation.value?.id ?: -1L)
    }

    fun selectPayeesByIds(payeeIds: LongArray) {
        viewModelScope.launch {
            try {
                val payees = payeeIds.toList().mapNotNull { payeeId ->
                    withContext(Dispatchers.IO) {
                        getPayeeByIdUseCase(payeeId)
                    }
                }
                _selectedPayees.value = payees
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun selectLocationById(locationId: Long) {
        viewModelScope.launch {
            try {
                val location = withContext(Dispatchers.IO) {
                    getLocationByIdUseCase(locationId)
                }
                if (location != null) {
                    _selectedLocation.value = location
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun removePayee(payee: PayeeTransaction) {
        _selectedPayees.value = _selectedPayees.value.filter { it.id != payee.id }
    }

    fun removeLocation() {
        _selectedLocation.value = null
    }

    fun navigateBack() {
        navigator.popBackStack()
    }

    fun resetTransactionState() {
        resetState()
    }
}