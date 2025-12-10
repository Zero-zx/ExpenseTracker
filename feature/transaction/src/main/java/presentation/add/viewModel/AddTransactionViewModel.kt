package presentation.add.viewModel

import account.model.Account
import account.usecase.GetAccountByIdUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.Category
import transaction.model.Event
import transaction.model.Location
import transaction.model.PayeeTransaction
import transaction.model.TransactionImage
import transaction.usecase.GetCategoriesUseCase
import transaction.usecase.GetEventByIdUseCase
import transaction.usecase.GetLocationByIdUseCase
import transaction.usecase.GetPayeeByIdUseCase
import transaction.usecase.SaveTransactionImageUseCase
import transaction.usecase.DeleteTransactionImagesUseCase
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
    private val getPayeeByIdUseCase: GetPayeeByIdUseCase,
    private val saveTransactionImageUseCase: SaveTransactionImageUseCase,
    private val deleteTransactionImagesUseCase: DeleteTransactionImagesUseCase
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

    private val _transactionImage = MutableStateFlow<TransactionImage?>(null)
    val transactionImage = _transactionImage.asStateFlow()

    private val _imageUploadState = MutableStateFlow<UIState<TransactionImage>?>(null)
    val imageUploadState = _imageUploadState.asStateFlow()

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
                val account = getAccountByIdUseCase(accountId)
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
                val event = getEventByIdUseCase(eventId)
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
                    getPayeeByIdUseCase(payeeId)
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
                val location = getLocationByIdUseCase(locationId)
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

    // Image management functions
    fun saveImage(imageUri: android.net.Uri) {
        viewModelScope.launch {
            try {
                _imageUploadState.value = UIState.Loading

                // Delete old image if exists
                _transactionImage.value?.let { oldImage ->
                    deleteTransactionImagesUseCase.deleteSingle(oldImage)
                }

                val result = saveTransactionImageUseCase(imageUri)

                result.onSuccess { transactionImage ->
                    _transactionImage.value = transactionImage
                    _imageUploadState.value = UIState.Success(transactionImage)
                }.onFailure { error ->
                    _imageUploadState.value = UIState.Error(
                        error.message ?: "Failed to save image"
                    )
                }
            } catch (e: Exception) {
                _imageUploadState.value = UIState.Error(
                    e.message ?: "Failed to save image"
                )
            }
        }
    }

    fun removeImage() {
        viewModelScope.launch {
            try {
                val currentImage = _transactionImage.value ?: return@launch

                val result = deleteTransactionImagesUseCase.deleteSingle(currentImage)

                result.onSuccess {
                    _transactionImage.value = null
                }.onFailure { error ->
                    _imageUploadState.value = UIState.Error(
                        error.message ?: "Failed to delete image"
                    )
                }
            } catch (e: Exception) {
                _imageUploadState.value = UIState.Error(
                    e.message ?: "Failed to delete image"
                )
            }
        }
    }

    fun clearImageUploadState() {
        _imageUploadState.value = null
    }

    fun resetTransactionState() {
        resetState()
    }
}