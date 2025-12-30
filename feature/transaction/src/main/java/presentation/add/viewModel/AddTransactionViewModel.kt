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
import category.usecase.GetCategoryByTypeUseCase
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
import presentation.add.model.TransactionType
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
    private val getCategoryByTypeUseCase: GetCategoryByTypeUseCase,
    private val getCategoryById: GetCategoryByIdUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
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

    private val _currentCategoryType = MutableStateFlow(CategoryType.EXPENSE)
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
                }
                .collect { accounts ->
                    // Select first account if no account is selected yet
                    if (_selectedAccount.value == null && accounts.isNotEmpty()) {
                        _selectedAccount.value = accounts.first()
                    }
                }
        }
    }

    fun updateCategoryType(transactionType: TransactionType) {
        when (transactionType) {
            TransactionType.EXPENSE -> {
                _currentCategoryType.value = CategoryType.EXPENSE
                _selectedCategory.value = null
            }

            TransactionType.INCOME -> {
                _currentCategoryType.value = CategoryType.INCOME
                _selectedCategory.value = null
            }

            TransactionType.LEND -> {
                viewModelScope.launch {
                    _currentCategoryType.value = CategoryType.LEND
                    _selectedCategory.value = getCategoryByTypeUseCase(CategoryType.LEND)
                }
            }

            TransactionType.BORROWING -> {
                viewModelScope.launch {
                    _currentCategoryType.value = CategoryType.BORROWING
                    _selectedCategory.value = getCategoryByTypeUseCase(CategoryType.BORROWING)
                }
            }

            else -> {
                _selectedCategory.value = null
            }
        }
    }

    fun addTransaction(
        amount: Double,
        description: String?,
        createAt: Long,
        repaymentDate: Long? = null
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

                val finalLocation = _selectedLocation.value?.let { location ->
                    addLocationUseCase(
                        name = location.name,
                        accountId = location.accountId
                    )
                }

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
                        repaymentDate = repaymentDate
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
                        repaymentDate = repaymentDate
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
        navigator.navigateToSelectBorrower(
            _selectedBorrower.value?.name ?: "",
            if (_selectedCategory.value?.type == CategoryType.BORROWING) PayeeType.BORROWER.name
            else PayeeType.LENDER.name
        )
    }

    fun toSelectLocation() {
        navigator.navigateToSelectLocation(_selectedLocation.value?.name ?: "")
    }

    fun selectLocation(locationName: String) {
        val accountId = getCurrentAccountIdUseCase() ?: _selectedAccount.value?.id ?: 1L
        _selectedLocation.value = Location(
            name = locationName,
            accountId = accountId
        )
    }

    fun selectLocationById(locationId: Long) {
        viewModelScope.launch {
            try {
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
        _selectedPayees.value = _selectedPayees.value.filter { it.name != payee.name }
    }

    fun removeBorrower() {
        _selectedBorrower.value = null
    }

    fun removeLocation() {
        _selectedLocation.value = null
    }

    fun selectBorrower(name: String) {
        _selectedBorrower.value = Payee(
            name = name,
            payeeType = if (_selectedCategory.value?.type == CategoryType.BORROWING) PayeeType.BORROWER else PayeeType.LENDER
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

    fun clearData() {
        _transactionId.value = null
        _selectedCategory.value = null
        _selectedEvent.value = null
        _selectedPayees.value = emptyList()
        _selectedBorrower.value = null
        _selectedLocation.value = null
        _transactionImage.value = null
    }
    private val _transactionLoaded = MutableStateFlow<Transaction?>(null)
    val transactionLoaded = _transactionLoaded.asStateFlow()

    fun loadTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                val transaction = getTransactionByIdUseCase(transactionId)
                if (transaction != null) {
                    _transactionId.value = transaction.id
                    _currentCategoryType.value = transaction.category.type
                    _selectedCategory.value = transaction.category
                    _selectedAccount.value = transaction.account
                    _selectedEvent.value = transaction.event
                    _selectedLocation.value = transaction.location

                    // Load payees
                    _selectedPayees.value = transaction.payees

                    // Load borrower and lender
                    _selectedBorrower.value = transaction.borrower

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