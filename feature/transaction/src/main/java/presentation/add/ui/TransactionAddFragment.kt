package presentation.add.ui

//import com.bumptech.glide.Glide
import account.model.Account
import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import base.BaseFragment
import base.UIState
import camera.CameraHandler
import category.model.Category
import category.model.CategoryType
import com.example.transaction.databinding.FragmentTransactionAddBinding
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import constants.FragmentResultKeys.REQUEST_SELECT_ACCOUNT_ID
import constants.FragmentResultKeys.REQUEST_SELECT_BORROWER_NAME
import constants.FragmentResultKeys.REQUEST_SELECT_CATEGORY_ID
import constants.FragmentResultKeys.REQUEST_SELECT_EVENT_NAME
import constants.FragmentResultKeys.REQUEST_SELECT_LOCATION_ID
import constants.FragmentResultKeys.REQUEST_SELECT_PAYEE_NAMES
import constants.FragmentResultKeys.RESULT_ACCOUNT_ID
import constants.FragmentResultKeys.RESULT_BORROWER_NAME
import constants.FragmentResultKeys.RESULT_CATEGORY_ID
import constants.FragmentResultKeys.RESULT_EVENT_NAME
import constants.FragmentResultKeys.RESULT_LOCATION_ID
import constants.FragmentResultKeys.RESULT_PAYEE_NAMES
import dagger.hilt.android.AndroidEntryPoint
import helpers.standardize
import payee.model.Payee
import permission.PermissionHandler
import presentation.add.adapter.CategoryAdapter
import presentation.add.adapter.CategoryDropdownAdapter
import presentation.add.model.TransactionType
import presentation.add.viewModel.AddTransactionViewModel
import storage.FileProvider
import transaction.model.Event
import transaction.model.Location
import ui.CalculatorManager
import ui.CalculatorProvider
import ui.GridSpacingItemDecoration
import ui.animateChevronRotation
import ui.createSlideDownAnimation
import ui.createSlideUpAnimation
import ui.gone
import ui.listenForSelectionResult
import ui.navigateBack
import ui.openDatePicker
import ui.openTimePicker
import ui.showNotImplementToast
import ui.showSuccessToast
import ui.showWarningToast
import ui.visible
import javax.inject.Inject
import com.example.common.R as CommonR


@AndroidEntryPoint
class TransactionAddFragment : BaseFragment<FragmentTransactionAddBinding>(
    FragmentTransactionAddBinding::inflate
) {
    private val viewModel: AddTransactionViewModel by viewModels()

    @Inject
    lateinit var fileProvider: FileProvider

    private val adapter = CategoryAdapter(
        onItemClick = { category ->
            viewModel.selectCategory(category = category)
        })

    private lateinit var cameraHandler: CameraHandler
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var calculatorManager: CalculatorManager
    private lateinit var categoryDropdownAdapter: CategoryDropdownAdapter

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionHandler.handlePermissionResult(permissions)
    }

    // store selected date (start-of-day millis) and time (offset millis from midnight)
    private var selectedDateStartMillis: Long? = null
    private var selectedTimeOffsetMillis: Long? = null

    // Get transaction ID from arguments if editing
    private val transactionId: Long? by lazy {
        arguments?.getLong("transaction_id", -1L)?.takeIf { it != -1L }
    }

    private val isEditMode: Boolean
        get() = transactionId != null

    override fun initView() {
        setUpDropdownMenu()
        setUpRecyclerView()
        listenForResult()
        setupPermissionHandler()
        setupCameraHandler()
        setupCalculator()

        // Load transaction data if editing
        transactionId?.let { id ->
            viewModel.loadTransaction(id)
        }

        if (isEditMode) {
            binding.buttonListTransaction.setImageResource(CommonR.drawable.v2_ic_back_gray)
        }
    }

    fun setUpDropdownMenu() {
        val items = TransactionType.entries
        categoryDropdownAdapter = CategoryDropdownAdapter(requireContext(), items)
        categoryDropdownAdapter.selectedPosition = 0

        (binding.dropdownMenuTransaction.editText as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(categoryDropdownAdapter)

            setOnItemClickListener { parent, _, position, _ ->
                val transactionType = parent.getItemAtPosition(position) as TransactionType
                viewModel.updateCategoryType(transactionType)
                setText(transactionType.label)
            }

            post {
                val dropdownWidth = (300 * resources.displayMetrics.density).toInt()
                dropDownWidth = dropdownWidth
                val buttonWidth = this.width
                dropDownHorizontalOffset = -(dropdownWidth - buttonWidth) / 2
                setDropDownBackgroundResource(CommonR.drawable.rounded_background)
            }
        }
    }

    fun getColorByCategoryType(categoryType: CategoryType, context: Context): Int {
        return when (categoryType) {
            CategoryType.EXPENSE -> context.getColor(CommonR.color.red_expense)
            CategoryType.INCOME -> context.getColor(CommonR.color.green_income)
            else -> context.getColor(CommonR.color.red_expense)
        }
    }

    fun setUpRecyclerView() {
        binding.recyclerViewCategories.apply {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(GridSpacingItemDecoration(4, 8, false))
        }.adapter = adapter
    }

    fun listenForResult() {
        // Listen for category selection result
        listenForSelectionResult(REQUEST_SELECT_CATEGORY_ID) { bundle ->
            val categoryId = bundle.getLong(RESULT_CATEGORY_ID)
            viewModel.selectCategoryById(categoryId)
        }

        // Listen for account selection result
        listenForSelectionResult(REQUEST_SELECT_ACCOUNT_ID) { bundle ->
            val accountId = bundle.getLong(RESULT_ACCOUNT_ID)
            viewModel.selectAccountById(accountId)
        }

        listenForSelectionResult(REQUEST_SELECT_EVENT_NAME) { bundle ->
            val eventName = bundle.getString(RESULT_EVENT_NAME)
            if (eventName != null) {
                viewModel.selectEvent(eventName)
            }
        }

        listenForSelectionResult(REQUEST_SELECT_PAYEE_NAMES) { bundle ->
            val payeeNames = bundle.getStringArray(RESULT_PAYEE_NAMES) ?: emptyArray()
            viewModel.addPayee(payeeNames.toList())
        }

        listenForSelectionResult(REQUEST_SELECT_LOCATION_ID) { bundle ->
            val locationId = bundle.getLong(RESULT_LOCATION_ID)
            viewModel.selectLocationById(locationId)
        }

        listenForSelectionResult(REQUEST_SELECT_BORROWER_NAME) { bundle ->
            val borrowerName = bundle.getString(RESULT_BORROWER_NAME)
            if (borrowerName != null) {
                viewModel.selectBorrower(borrowerName)
            }
        }
    }

    override fun initListener() {
        binding.apply {
            buttonListTransaction.setOnClickListener {
                if (isEditMode) navigateBack()
                else viewModel.onHistoryClick()
            }

            layoutMoreCategory.setOnClickListener {
                viewModel.toSelectCategory()
            }

            buttonMostUse.setOnClickListener {
                toggleRecentlyCategory()
            }

            buttonSelectWallet.setOnClickListener {
                viewModel.toSelectAccount()
            }

            customViewEvent.setOnClickListener {
                viewModel.toSelectEvent()
            }

            customViewPayee.setOnClickListener {
                viewModel.toSelectPayee()
            }

            buttonSelectBorrower.setOnClickListener {
                viewModel.toSelectBorrower()
            }

            customViewLocation.setOnClickListener {
                viewModel.toSelectLocation()
            }

            textViewDate.setOnClickListener {
                openDatePicker(textViewDate) { startOfDayMillis ->
                    selectedDateStartMillis = startOfDayMillis
                }
            }

            textViewTime.setOnClickListener {
                openTimePicker(textViewTime) { offsetMillis ->
                    selectedTimeOffsetMillis = offsetMillis
                }
            }

            buttonShowMore.setOnClickListener {
                viewModel.toggleMoreDetailsExpanded()
            }

            buttonSubmit.setOnClickListener {
                saveTransaction()
            }

            buttonSave.setOnClickListener {
                saveTransaction()
            }

            // Take photo
            buttonTakePhoto.setOnClickListener {
                permissionHandler.checkCameraPermission()
                // After permission granted, launch camera
                permissionHandler = PermissionHandler(
                    fragment = this@TransactionAddFragment,
                    onGranted = { cameraHandler.launchCamera() },
                    onDenied = {
                        Toast.makeText(
                            requireContext(), "Camera permission required", Toast.LENGTH_SHORT
                        ).show()
                    })
                permissionHandler.setup(permissionLauncher)
            }

            // Pick from gallery
            buttonPickImage.setOnClickListener {
                permissionHandler.checkGalleryPermission()
                permissionHandler = PermissionHandler(
                    fragment = this@TransactionAddFragment,
                    onGranted = { cameraHandler.launchGallery() },
                    onDenied = {
                        Toast.makeText(
                            requireContext(), "Gallery permission required", Toast.LENGTH_SHORT
                        ).show()
                    })
                permissionHandler.setup(permissionLauncher)
            }

            ivMic.setOnClickListener {
                showNotImplementToast()
            }

            ivChat.setOnClickListener {
                showNotImplementToast()
            }

            ivScanInvoice.setOnClickListener {
                showNotImplementToast()
            }

            dateViewBorrow.apply {
                setOnClickListener {
                    openDatePicker(dateViewBorrow.getTextView(), false) { startOfDayMillis ->
                        selectedDateStartMillis = startOfDayMillis
                        showEndDrawable()
                    }
                }

                setEndDrawableClickListener {
                    clearText()
                }
            }
        }
    }

    override fun observeData() {
        collectState(viewModel.categoryState) { state ->
            when (state) {
                is UIState.Loading -> {
                    // Show loading indicator if needed
                }

                is UIState.Success -> {
                    showCategories(state.data.take(8))
                }

                is UIState.Error -> {
                    context?.showWarningToast("Error loading categories: ${state.message}")
                }

                else -> {}
            }
        }

        collectState(viewModel.currentCategoryType) { categoryType ->
            updateUIForCategoryType(categoryType)
        }

        collectState(viewModel.selectedCategory) { category ->
            adapter.setSelectedCategory(category)
            category?.let {
                updateSelectedCategory(it)
                updateDropdownForCategoryType(it.type)
            }
        }

        collectState(viewModel.selectedAccount) { account ->
            account?.let { updateSelectedAccount(it) }
        }

        collectState(viewModel.selectedEvent) { event ->
            if (event != null) {
                updateSelectedEvent(event)
            }
        }

        collectState(viewModel.selectedPayees) { payees ->
            updateSelectedPayees(payees)
        }

        collectState(viewModel.selectedBorrower) { borrower ->
            updateSelectedBorrower(borrower)
        }

        collectState(viewModel.selectedLocation) { location ->
            updateSelectedLocation(location)
        }

        collectState(viewModel.transactionImage) { image ->
            // Update UI to show/hide image
            if (image != null) {
                // Show image in your UI
                binding.layoutImage.visible()
                binding.imageView.visible()
//                Glide.with(this).load(image.getFullPath(requireContext())).into(binding.imageView)
            } else {
                // Hide image
                binding.layoutImage.gone()
            }
        }

        collectState(viewModel.imageUploadState) { state ->
            when (state) {
                is UIState.Loading -> {
                }

                is UIState.Success -> {
                    viewModel.clearImageUploadState()
                }

                is UIState.Error -> {
                    // Show error message
                    context?.showWarningToast("Error: ${state.message}")
                    viewModel.clearImageUploadState()
                }

                null -> {
                    // No state
                }

                else -> {}
            }
        }

        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {
                    val message = if (transactionId != null) {
                        "Transaction updated successfully"
                    } else {
                        "Transaction added successfully"
                    }
                    showSuccessToast(message)
                }

                is UIState.Error -> {
                    val message = if (transactionId != null) {
                        "Error updating transaction: ${state.message}"
                    } else {
                        "Error adding transaction: ${state.message}"
                    }
                    context?.showWarningToast(message)
                }

                else -> {}
            }
        }

        // Observe transaction data when loaded for editing
        collectState(viewModel.transactionLoaded) { transaction ->
            transaction?.let {
                populateTransactionFields(it)
            }
        }

        // Observe more details expanded state
        collectState(viewModel.isMoreDetailsExpanded) { isExpanded ->
            updateMoreDetailsVisibility(isExpanded)
        }
    }

    private fun saveTransaction() {
        val selectedCategory = viewModel.selectedCategory.value
        if (selectedCategory == null) {
            context?.showWarningToast("Please select a category")
            return
        }

        // Parse amount by removing thousand separators (commas, spaces, etc.)
        val amountText = binding.editTextAmount.text.toString().replace(",", "")  // Remove commas
            .replace(" ", "")  // Remove spaces
            .trim()
        val amount = amountText.toDoubleOrNull() ?: 0.0

        viewModel.addTransaction(
            amount = amount,
            description = binding.editTextNote.text?.toString(),
            createAt = selectedDateStartMillis?.plus(selectedTimeOffsetMillis ?: 0)
                ?: System.currentTimeMillis()
        )
    }

    private fun updateSelectedCategory(category: Category) {
        binding.apply {
            // Update icon
            iconCategory.setImageResource(category.iconRes)
            // Update category name
            textViewCategory.text = category.title.standardize()
        }
    }

    private fun updateUIForCategoryType(categoryType: CategoryType) {
        binding.apply {
            hideAllLayout()
            when (categoryType) {
                CategoryType.EXPENSE -> {
                    divider.visible()
                    layoutMostUse.visible()
                }

                CategoryType.INCOME -> {
                    hideAllLayout()
                }

                CategoryType.LEND -> {
                    divider.visible()
                    dateViewBorrow.visible()
                    dateViewBorrow.setStartDrawable(CommonR.drawable.ic_date_thu_no)
                    buttonSelectBorrower.visible()
                    buttonSelectBorrower.getTextView().text = "Select borrower"
                }

                CategoryType.BORROWING -> {
                    dateViewBorrow.visible()
                    dateViewBorrow.setStartDrawable(CommonR.drawable.ic_date_tra_no)
                    divider.visible()
                    buttonSelectBorrower.visible()
                    buttonSelectBorrower.getTextView().text = "Select lender"
                }

                else -> {}
            }

            binding.iconCategory.setImageResource(CommonR.drawable.ic_add_category_light)
            dropdownMenuTransaction.editText?.setText(categoryType.label)
            context?.let {
                val color = getColorByCategoryType(categoryType, it)
                binding.editTextAmount.apply {
                    setTextColor(color)
                    background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                binding.textViewCurrency.setTextColor(color)
            }
        }
    }

    private fun hideAllLayout() {
        binding.apply {
            divider.gone()
            layoutMostUse.gone()
            layoutMore.gone()
            buttonSelectBorrower.gone()
            dateViewBorrow.apply {
                gone()
                hideEndDrawable()
            }
        }
    }

    private fun updateDropdownForCategoryType(categoryType: CategoryType) {
        // Find the position of the category type in the dropdown

        val position = when (categoryType) {
            CategoryType.COLLECT_DEBT -> CategoryType.entries.indexOf(CategoryType.INCOME)
            CategoryType.REPAYMENT -> CategoryType.entries.indexOf(CategoryType.EXPENSE)
            else -> {
                CategoryType.entries.indexOf(categoryType)
            }
        }
        if (position != -1) {
            categoryDropdownAdapter.selectedPosition = position

//            // Update the dropdown text without triggering the listener
            (binding.dropdownMenuTransaction.editText as? AutoCompleteTextView)?.apply {
                setText(TransactionType.entries[position].label, false)
            }
        }
    }


    private fun updateSelectedAccount(account: Account) {
        binding.apply {
            textViewAccountName.text = account.username.standardize()
        }
    }

    private fun updateSelectedEvent(event: Event) {
        binding.apply {
            customViewEvent.getTextView().isVisible = true
            customViewEvent.getChipGroup().removeAllViews()
            customViewEvent.hideText()

            // Add chips for each selected event (filter out nulls for safety)
            val chip = Chip(requireContext())
            chip.text = event.eventName
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                viewModel.removeEvent(event)
            }
            // Insert before the "Add Event" chip
            customViewEvent.getChipGroup()
                .addView(chip, customViewEvent.getChipGroup().childCount - 1)

        }
    }

    private fun updateSelectedPayees(payees: List<Payee>) {
        binding.apply {
            customViewPayee.getTextView().isVisible = payees.isEmpty()
            customViewPayee.getChipGroup().removeAllViews()

            // Add chips for each selected payee
            payees.forEach { payee ->
                val chip = com.google.android.material.chip.Chip(requireContext())
                chip.text = payee.name.standardize()
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    viewModel.removePayee(payee)
                }
                customViewPayee.getChipGroup()
                    .addView(chip, customViewPayee.getChipGroup().childCount - 1)
            }
        }
    }

    private fun updateSelectedLocation(location: Location?) {
        binding.apply {
            customViewLocation.getTextView().isVisible = location == null
            customViewLocation.getChipGroup().removeAllViews()

            location?.let { loc ->
                val chip = Chip(requireContext())
                chip.text = loc.name.standardize()
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    viewModel.removeLocation()
                }
                customViewLocation.getChipGroup()
                    .addView(chip, customViewLocation.getChipGroup().childCount - 1)
            }
        }
    }

    private fun updateSelectedBorrower(borrower: Payee?) {
        binding.apply {
            buttonSelectBorrower.getTextView().isVisible = borrower == null
            buttonSelectBorrower.getChipGroup().removeAllViews()

            borrower?.let {
                buttonSelectBorrower.addChip(borrower.name, true) {
                    viewModel.removeBorrower()
                }
            }
        }
    }

    private fun updateSelectedLender(lender: Payee?) {
        binding.apply {
            if (buttonSelectBorrower.isVisible) {
                buttonSelectBorrower.getTextView().text = lender?.name?.standardize()
                    ?: when (viewModel.currentCategoryType.value) {
                        CategoryType.LEND -> "Select borrower"
                        CategoryType.BORROWING -> "Select lender"
                        else -> "Select"
                    }
            }
        }
    }


    fun toggleRecentlyCategory() {
        binding.apply {
            // Toggle visibility first
            recyclerViewCategories.isVisible = !recyclerViewCategories.isVisible

            // Then animate chevron to match the new state
            imageViewChevron.animateChevronRotation(
                isExpanded = recyclerViewCategories.isVisible,
                origin = -90f,
                angle = 90f,
            )
        }
    }

    private fun showCategories(categories: List<Category>) {
        adapter.submitList(categories)
    }

    private fun setupPermissionHandler() {
        permissionHandler = PermissionHandler(
            fragment = this,
            onGranted = { /* Will be set dynamically */ },
            onDenied = {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            })
        permissionHandler.setup(permissionLauncher)
    }

    private fun setupCameraHandler() {
        cameraHandler =
            CameraHandler(fragment = this, fileProvider = fileProvider, onImageCaptured = { uri ->
                viewModel.saveImage(uri)
            }, onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            })
        cameraHandler.setup()
    }

    override fun onResume() {
        super.onResume()
        // Only reset state if not editing
        if (transactionId == null) {
            viewModel.resetTransactionState()
        }
    }

    private fun populateTransactionFields(transaction: transaction.model.Transaction) {
        binding.apply {
            // Populate amount
            editTextAmount.setText(transaction.amount.toString())

            // Populate description
            editTextNote.setText(transaction.description ?: "")

            // Populate date and time
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = transaction.createAt
            }
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

            textViewDate.text = dateFormat.format(calendar.time)
            textViewTime.text = timeFormat.format(calendar.time)

            // Set date and time millis for submission
            val localDate = java.time.Instant.ofEpochMilli(transaction.createAt)
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            selectedDateStartMillis =
                localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)
            selectedTimeOffsetMillis = (hour * 3_600_000L + minute * 60_000L)
        }
    }

    private fun updateMoreDetailsVisibility(isExpanded: Boolean) {
        binding.apply {
            if (isExpanded) {
                layoutMore.visibility = View.VISIBLE
                buttonShowMore.text = getString(com.example.common.R.string.text_hide_details)
                layoutMore.startAnimation(createSlideDownAnimation(context))
            } else {
                if (layoutMore.visibility == View.VISIBLE) {
                    buttonShowMore.text =
                        getString(com.example.common.R.string.text_show_more_details)
                    layoutMore.startAnimation(createSlideUpAnimation(context, layoutMore))
                } else {
                    // Set visibility directly without animation when initializing
                    layoutMore.visibility = View.GONE
                    buttonShowMore.text =
                        getString(com.example.common.R.string.text_show_more_details)
                }
            }
        }
    }

    private fun setupCalculator() {
        val calculatorProvider = activity as? CalculatorProvider
        val calculatorView = calculatorProvider?.getCalculatorView()

        if (calculatorView != null) {
            calculatorManager = CalculatorManager(
                calculatorView = calculatorView,
                amountEditText = binding.editTextAmount,
                context = requireContext()
            )
        }
    }

    override fun onStop() {
        super.onStop()
        calculatorManager.hide()
    }

    companion object {
        private const val ARG_TRANSACTION_ID = "transaction_id"

        // Add mode
        fun newInstance(): TransactionAddFragment = TransactionAddFragment()

        // Edit mode
        fun newInstance(transactionId: Long): TransactionAddFragment {
            return TransactionAddFragment().apply {
                arguments = bundleOf(ARG_TRANSACTION_ID to transactionId)
            }
        }
    }
}
