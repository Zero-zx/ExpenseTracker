package presentation.add.viewModel

import account.model.Account
import account.usecase.GetAccountByIdUseCase
import account.usecase.GetAccountsUseCase
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
import session.usecase.GetCurrentAccountIdUseCase
import transaction.model.Category
import transaction.model.CategoryType
import transaction.model.Event
import transaction.model.Location
import transaction.model.Payee
import transaction.model.Transaction
import transaction.model.TransactionImage
import transaction.repository.TransactionImageRepository
import transaction.usecase.AddEventUseCase
import transaction.usecase.AddLocationUseCase
import transaction.usecase.AddPayeeUseCase
import transaction.usecase.DeleteTransactionImagesUseCase
import transaction.usecase.GetCategoriesByTypeUseCase
import transaction.usecase.GetCategoryByIdUseCase
import transaction.usecase.GetLocationByIdUseCase
import transaction.usecase.GetPayeeByIdUseCase
import transaction.usecase.GetTransactionByIdUseCase
import transaction.usecase.SaveTransactionImageUseCase
import usecase.AddTransactionUseCase
import usecase.UpdateTransactionUseCase
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val navigator: Navigator,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val getCategoriesByTypeUseCase: GetCategoriesByTypeUseCase,
    private val getCategoryById: GetCategoryByIdUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val getPayeeByIdUseCase: GetPayeeByIdUseCase,
    private val saveTransactionImageUseCase: SaveTransactionImageUseCase,
    private val deleteTransactionImagesUseCase: DeleteTransactionImagesUseCase,
    private val addEventUseCase: AddEventUseCase,
    private val addPayeeUseCase: AddPayeeUseCase,
    private val addLocationUseCase: AddLocationUseCase,
    private val imageRepository: TransactionImageRepository,
    private val getCurrentAccountIdUseCase: GetCurrentAccountIdUseCase
) : BaseViewModel<Long>() {

    private val _transactionId = MutableStateFlow<Long?>(null)
    val transactionId = _transactionId.asStateFlow()

    private val _categoryState = MutableStateFlow<UIState<List<Category>>>(UIState.Loading)
    val categoryState = _categoryState.asStateFlow()

    private val _currentCategoryType = MutableStateFlow<CategoryType>(CategoryType.EXPENSE)
    val currentCategoryType = _currentCategoryType.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount = _selectedAccount.asStateFlow()

    private val _selectedEvent = MutableStateFlow<Event?>(null)
    val selectedEvent = _selectedEvent.asStateFlow()

    private val _selectedPayees = MutableStateFlow<List<Payee>>(emptyList())
    val selectedPayees = _selectedPayees.asStateFlow()

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    private val _transactionImage = MutableStateFlow<TransactionImage?>(null)
    val transactionImage = _transactionImage.asStateFlow()

    private val _imageUploadState = MutableStateFlow<UIState<TransactionImage>?>(null)
    val imageUploadState = _imageUploadState.asStateFlow()

    // UI state for more details section
    private val _isMoreDetailsExpanded = MutableStateFlow(false)
    val isMoreDetailsExpanded = _isMoreDetailsExpanded.asStateFlow()


    init {
        loadCategoriesByType(CategoryType.EXPENSE)
        loadDefaultAccount()
    }

    fun loadCategoriesByType(type: CategoryType) {
        _currentCategoryType.value = type
        viewModelScope.launch {
            getCategoriesByTypeUseCase(type)
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

    private fun loadDefaultAccount() {
        viewModelScope.launch {
            getAccountsUseCase()
                .catch { exception ->
                    // Silently fail - user can select account manually
                }
                .collect { accounts ->
                    // Select first account if no account is selected yet
                    if (_selectedAccount.value == null && accounts.isNotEmpty()) {
                        _selectedAccount.value = accounts.first()
                    }
                }
        }
    }

    fun updateCategoryType(categoryType: CategoryType) {
        _currentCategoryType.value = categoryType
    }

    fun addTransaction(
        amount: Double,
        description: String?,
        createAt: Long
    ) {
        viewModelScope.launch {
            val selectedCategory = _selectedCategory.value
            val selectedAccount = _selectedAccount.value
            val selectedEvent = _selectedEvent.value

            if (selectedCategory == null) {
                setError("Please select a category")
                return@launch
            }

            if (selectedAccount == null) {
                setError("Please select an account")
                return@launch
            }

            setLoading()
            try {
                // Step 1: Persist event if it exists
                // AddEventUseCase will check for duplicate name and return existing event if found,
                // or create new one and return it with generated ID
                val finalEvent = selectedEvent?.let { event ->
                    addEventUseCase(
                        eventName = event.eventName,
                        startDate = event.startDate,
                        endDate = event.endDate,
                        numberOfParticipants = event.numberOfParticipants ?: 0,
                        accountId = event.accountId,
                        participants = event.participants
                    )
                }

                // Step 2: Persist payees if they exist
                // AddPayeeUseCase will check for duplicate name and return existing payee if found,
                // or create new one and return it with generated ID
                val finalPayees = _selectedPayees.value.map { payee ->
                    addPayeeUseCase(
                        name = payee.name,
                        isFromContacts = payee.isFromContacts,
                        contactId = payee.contactId
                    )
                }

                val finalLocation = _selectedLocation.value

                // Step 3: Save or update transaction with persisted entities
                val currentTransactionId = _transactionId.value
                val finalTransactionId = if (currentTransactionId != null) {
                    // Update existing transaction
                    updateTransactionUseCase(
                        transactionId = currentTransactionId,
                        amount = amount,
                        category = selectedCategory,
                        account = selectedAccount,
                        event = finalEvent,
                        description = description,
                        createAt = createAt,
                        location = finalLocation,
                        payees = finalPayees
                    )
                    currentTransactionId
                } else {
                    // Create new transaction
                    addTransactionUseCase(
                        amount = amount,
                        category = selectedCategory,
                        account = selectedAccount,
                        event = finalEvent,
                        description = description,
                        createAt = createAt,
                        location = finalLocation,
                        payees = finalPayees
                    )
                }

                // Step 5: Link image to transaction if exists
                _transactionImage.value?.let { image ->
                    val imageWithTransactionId = image.copy(transactionId = finalTransactionId)
                    imageRepository.insertImage(imageWithTransactionId)
                }

                setSuccess(finalTransactionId)
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        // Load categories of the same type if different from current
        if (_categoryState.value is UIState.Success) {
            val currentCategories = (_categoryState.value as UIState.Success).data
            if (currentCategories.firstOrNull()?.type != category.type) {
                loadCategoriesByType(category.type)
            }
        }
    }

    fun selectCategoryById(categoryId: Long) {
        viewModelScope.launch {
            try {
                val category = getCategoryById(categoryId)
                _currentCategoryType.value = category.type
                _selectedCategory.value = category
            } catch (e: Exception) {
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

    fun selectEvent(eventName: String) {
        _selectedEvent.value = Event(
            id = -1L,
            eventName = eventName,
            startDate = System.currentTimeMillis(),
            endDate = null,
            accountId = 1L,
            isActive = true
        )
    }

    fun addPayee(payeeName: List<String>) {
        _selectedPayees.value = payeeName.map {
            Payee(
                id = -1L,
                name = it
            )
        }
    }

    fun removeEvent(event: Event) {
        _selectedEvent.value = null
    }

    fun onHistoryClick() {
        navigator.navigateToTransaction()
    }

    fun navigateBack() {
        navigator.popBackStack()
    }

    fun toSelectCategory() {
        navigator.navigateToMoreCategory(_currentCategoryType.value.name)
    }

    fun toSelectAccount() {
        navigator.navigateToSelectAccount(selectedAccount.value?.id ?: -1L)
    }

    fun toSelectEvent() {
        navigator.navigateToSelectEvent(selectedEvent.value?.eventName ?: "")
    }

    fun toSelectPayee() {
        val selectedPayeeNames = _selectedPayees.value.map { it.name }.toTypedArray()
        navigator.navigateToSelectPayee(selectedPayeeNames)
    }

    fun toSelectLocation() {
        navigator.navigateToSelectLocation(_selectedLocation.value?.id ?: -1L)
    }


//    fun selectPayeesByNames(payeeIds: LongArray) {
//        viewModelScope.launch {
//            try {
//                val payees = payeeIds.toList().mapNotNull { payeeId ->
//                    // Check temporary payees first (negative IDs)
//                    if (payeeId < 0) {
//                        _temporaryPayees.value.find { it.id == payeeId }
//                    } else {
//                        getPayeeByIdUseCase(payeeId)
//                    }
//                }
//                _selectedPayees.value = payees
//            } catch (e: Exception) {
//                // Handle error if needed
//            }
//        }
//    }

    fun selectLocationById(locationId: Long) {
        viewModelScope.launch {
            try {
                // Only handle positive IDs (persisted locations)
                // Temporary locations should be persisted via addTemporaryLocation
                if (locationId > 0) {
                    val location = getLocationByIdUseCase(locationId)
                    if (location != null) {
                        _selectedLocation.value = location
                    }
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun removePayee(payee: Payee) {
        // Match by name since temporary payees all have id = -1L
        _selectedPayees.value = _selectedPayees.value.filter { it.name != payee.name }
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

    fun addTemporaryLocation(location: Location): Long {
        viewModelScope.launch {
            try {
                // Persist location immediately when user creates it
                val persistedLocation = addLocationUseCase(
                    name = location.name,
                    accountId = location.accountId
                )
                _selectedLocation.value = persistedLocation
            } catch (e: Exception) {
                setError(e.message ?: "Failed to create location")
            }
        }
        // Return a placeholder ID for backward compatibility
        // The actual persisted location will be set in the state
        return -1L
    }


    private val _transactionLoaded = MutableStateFlow<Transaction?>(null)
    val transactionLoaded = _transactionLoaded.asStateFlow()

    fun loadTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                val transaction = getTransactionByIdUseCase(transactionId)
                if (transaction != null) {
                    _transactionId.value = transaction.id
                    _selectedCategory.value = transaction.category
                    _selectedAccount.value = transaction.account
                    _selectedEvent.value = transaction.event
                    _selectedLocation.value = transaction.location

                    // Load payees
                    _selectedPayees.value = transaction.payees

                    // Load image if exists
                    transaction.images?.let { image ->
                        _transactionImage.value = image
                    }

                    _transactionLoaded.value = transaction
                    resetState()
                } else {
                    setError("Transaction not found")
                }
            } catch (e: Exception) {
                setError(e.message ?: "Failed to load transaction")
            }
        }
    }

    /**
     * Get current account ID from session
     */
    fun getCurrentAccountId(): Long? {
        return getCurrentAccountIdUseCase()
    }

    /**
     * Toggle more details section expanded state
     */
    fun toggleMoreDetailsExpanded() {
        _isMoreDetailsExpanded.value = !_isMoreDetailsExpanded.value
    }
}