package presentation.add.viewModel

import account.model.Account
import account.usecase.GetAccountByIdUseCase
import account.usecase.GetAccountsUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import base.UIState
import category.model.Category
import category.model.CategoryType
import category.usecase.GetCategoriesByTypeUseCase
import category.usecase.GetCategoryByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import navigation.Navigator
import payee.model.Payee
import payee.model.PayeeType
import payee.usecase.AddPayeeUseCase
import payee.usecase.GetPayeeByIdUseCase
import session.usecase.GetCurrentAccountIdUseCase
import transaction.model.Event
import transaction.model.Location
import transaction.model.Transaction
import transaction.model.TransactionImage
import transaction.repository.TransactionImageRepository
import transaction.usecase.AddEventUseCase
import transaction.usecase.AddLocationUseCase
import transaction.usecase.DeleteTransactionImagesUseCase
import transaction.usecase.GetLocationByIdUseCase
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

    private val _selectedBorrower = MutableStateFlow<Payee?>(null)
    val selectedBorrower = _selectedBorrower.asStateFlow()

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    private val _selectedLender = MutableStateFlow<Payee?>(null)
    val selectedLender = _selectedLender.asStateFlow()

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
        _selectedCategory.value = null
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
                val finalEvent = selectedEvent?.let { event ->
                    addEventUseCase(
                        event
                    )
                }

                val finalPayees = _selectedPayees.value.map { payee ->
                    addPayeeUseCase(
                        payee
                    )
                }

                val finalBorrower = _selectedBorrower.value?.let { borrower ->
                    addPayeeUseCase(
                        borrower
                    )
                }

                // Step 2.6: Persist lender if it exists
                val finalLender = _selectedLender.value?.let { lender ->
                    addPayeeUseCase(
                        lender
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
                        payees = finalPayees,
                        borrower = finalBorrower,
                        lender = finalLender
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
                        payees = finalPayees,
                        borrower = finalBorrower,
                        lender = finalLender
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
                name = it,
                payeeType = PayeeType.PAYEE
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

    fun toSelectBorrower() {
        navigator.navigateToSelectBorrower(_selectedBorrower.value?.name ?: "")
    }

    fun toSelectLocation() {
        navigator.navigateToSelectLocation(_selectedLocation.value?.id ?: -1L)
    }

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

    fun selectBorrower(name: String) {
        _selectedBorrower.value = Payee(
            id = -1L,
            name = name,
            payeeType = PayeeType.BORROWER
        )
    }

    fun selectBorrowerById(borrowerId: Long) {
        viewModelScope.launch {
            try {
                if (borrowerId > 0) {
                    val borrower = getPayeeByIdUseCase(borrowerId)
                    if (borrower != null) {
                        _selectedBorrower.value = borrower
                    }
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }


    fun selectLender(name: String) {
        val currentAccountId = getCurrentAccountId() ?: 1L
        _selectedLender.value = Payee(
            id = -1L,
            name = name,
            payeeType = PayeeType.LENDER
        )
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

                    // Load borrower and lender
                    _selectedBorrower.value = transaction.borrower
                    _selectedLender.value = transaction.lender

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