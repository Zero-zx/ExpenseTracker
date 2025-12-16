package presentation.add.ui

//import com.bumptech.glide.Glide
import account.model.Account
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import base.BaseFragment
import base.UIState
import camera.CameraHandler
import com.example.transaction.databinding.FragmentTransactionAddBinding
import constants.FragmentResultKeys.REQUEST_SELECT_ACCOUNT_ID
import constants.FragmentResultKeys.REQUEST_SELECT_CATEGORY_ID
import constants.FragmentResultKeys.REQUEST_SELECT_EVENT_ID
import constants.FragmentResultKeys.REQUEST_SELECT_LOCATION_ID
import constants.FragmentResultKeys.REQUEST_SELECT_PAYEE_IDS
import constants.FragmentResultKeys.RESULT_ACCOUNT_ID
import constants.FragmentResultKeys.RESULT_CATEGORY_ID
import constants.FragmentResultKeys.RESULT_EVENT_ID
import constants.FragmentResultKeys.RESULT_LOCATION_ID
import constants.FragmentResultKeys.RESULT_PAYEE_IDS
import dagger.hilt.android.AndroidEntryPoint
import helpers.standardize
import permission.PermissionHandler
import presentation.add.adapter.CategoryAdapter
import presentation.add.adapter.CategoryDropdownAdapter
import presentation.add.viewModel.AddTransactionViewModel
import storage.FileProvider
import transaction.model.Category
import transaction.model.CategoryType
import transaction.model.Event
import ui.CalculatorManager
import ui.CalculatorProvider
import ui.GridSpacingItemDecoration
import ui.createSlideDownAnimation
import ui.createSlideUpAnimation
import ui.gone
import ui.listenForSelectionResult
import ui.openDatePicker
import ui.openTimePicker
import ui.visible
import javax.inject.Inject
import com.example.common.R as CommonR
import com.example.transaction.R as TransactionR


@AndroidEntryPoint
class AddTransactionFragment : BaseFragment<FragmentTransactionAddBinding>(
    FragmentTransactionAddBinding::inflate
) {
    private val viewModel: AddTransactionViewModel by hiltNavGraphViewModels(TransactionR.id.transaction_nav_graph)

    @Inject
    lateinit var fileProvider: FileProvider

    private val adapter = CategoryAdapter(
        onItemClick = { category ->
            viewModel.selectCategory(category = category)
        }
    )

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
    }

    fun setUpDropdownMenu() {
        val items = CategoryType.entries
        categoryDropdownAdapter = CategoryDropdownAdapter(requireContext(), items)
        categoryDropdownAdapter.selectedPosition = 0
        (binding.dropdownMenuTransaction.editText as? AutoCompleteTextView)?.apply {
            setAdapter(categoryDropdownAdapter)
            setOnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position) as CategoryType
                setText(selectedItem.label, false)
                // Load categories when user manually changes type
                viewModel.loadCategoriesByType(selectedItem)
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

        listenForSelectionResult(REQUEST_SELECT_EVENT_ID) { bundle ->
            val eventId = bundle.getLong(RESULT_EVENT_ID)
            viewModel.selectEventById(eventId)
        }

        listenForSelectionResult(REQUEST_SELECT_PAYEE_IDS) { bundle ->
            val payeeIds = bundle.getLongArray(RESULT_PAYEE_IDS) ?: longArrayOf()
            viewModel.selectPayeesByIds(payeeIds)
        }

        listenForSelectionResult(REQUEST_SELECT_LOCATION_ID) { bundle ->
            val locationId = bundle.getLong(RESULT_LOCATION_ID)
            viewModel.selectLocationById(locationId)
        }
    }


    override fun initListener() {
        binding.apply {
            buttonListTransaction.setOnClickListener {
                viewModel.onHistoryClick()
            }

            layoutCategorySelection.setOnClickListener {
                viewModel.toSelectCategory()
            }

            layoutRecentlyUse.setOnClickListener {
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
                if (layoutMore.visibility == View.GONE) {
                    layoutMore.visibility = View.VISIBLE
                    buttonShowMore.text = getString(com.example.common.R.string.text_hide_details)
                    layoutMore.startAnimation(
                        createSlideDownAnimation(context)
                    )
                } else {
                    buttonShowMore.text =
                        getString(com.example.common.R.string.text_show_more_details)
                    layoutMore.startAnimation(
                        createSlideUpAnimation(context, layoutMore)
                    )
                }
            }

            buttonSubmit.setOnClickListener {
                val selectedCategory = viewModel.selectedCategory.value
                if (selectedCategory == null) {
                    Toast.makeText(
                        context,
                        "Please select a category",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Parse amount by removing thousand separators (commas, spaces, etc.)
                val amountText = editTextAmount.text.toString()
                    .replace(",", "")  // Remove commas
                    .replace(" ", "")  // Remove spaces
                    .trim()
                val amount = amountText.toDoubleOrNull() ?: 0.0

                viewModel.addTransaction(
                    amount = amount,
                    description = editTextNote.text?.toString(),
                    createAt = selectedDateStartMillis?.plus(selectedTimeOffsetMillis ?: 0)
                        ?: System.currentTimeMillis()
                )
            }

            // Take photo
            buttonTakePhoto.setOnClickListener {
                permissionHandler.checkCameraPermission()
                // After permission granted, launch camera
                permissionHandler = PermissionHandler(
                    fragment = this@AddTransactionFragment,
                    onGranted = { cameraHandler.launchCamera() },
                    onDenied = {
                        Toast.makeText(
                            requireContext(),
                            "Camera permission required",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                permissionHandler.setup(permissionLauncher)
            }

            // Pick from gallery
            buttonPickImage.setOnClickListener {
                permissionHandler.checkGalleryPermission()
                permissionHandler = PermissionHandler(
                    fragment = this@AddTransactionFragment,
                    onGranted = { cameraHandler.launchGallery() },
                    onDenied = {
                        Toast.makeText(
                            requireContext(),
                            "Gallery permission required",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                permissionHandler.setup(permissionLauncher)
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
                    Toast.makeText(
                        context,
                        "Error loading categories: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }

        collectState(viewModel.selectedCategory) { category ->
            adapter.setSelectedCategory(category)
            category?.let {
                updateSelectedCategory(it)
                // Update dropdown menu to show the correct category type
                updateDropdownForCategoryType(it.type)
            }
        }

        collectState(viewModel.selectedAccount) { account ->
            account?.let { updateSelectedAccount(it) }
        }

        collectState(viewModel.selectedEvent) { event ->
            updateSelectedEvents(event?.let { listOf(it) } ?: emptyList())
        }

        collectState(viewModel.selectedPayees) { payees ->
            updateSelectedPayees(payees)
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
                    // Show loading indicator
                    Toast.makeText(context, "Uploading image...", Toast.LENGTH_SHORT).show()
                }

                is UIState.Success -> {
                    // Show success message
                    Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
                    viewModel.clearImageUploadState()
                }

                is UIState.Error -> {
                    // Show error message
                    Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UIState.Error -> {
                    val message = if (transactionId != null) {
                        "Error updating transaction: ${state.message}"
                    } else {
                        "Error adding transaction: ${state.message}"
                    }
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }

        // Observe transaction data when loaded for editing
        collectState<transaction.model.Transaction?>(viewModel.transactionLoaded) { transaction ->
            transaction?.let {
                populateTransactionFields(it)
            }
        }
    }

    private fun updateSelectedCategory(category: Category) {
        binding.apply {
            // Update icon
            iconCategory.setImageResource(category.iconRes)
            // Update category name
            textViewCategory.text = category.title.standardize()
        }
    }

    private fun updateDropdownForCategoryType(categoryType: CategoryType) {
        // Find the position of the category type in the dropdown
        val position = CategoryType.entries.indexOf(categoryType)
        if (position != -1) {
            categoryDropdownAdapter.selectedPosition = position

            // Update the dropdown text without triggering the listener
            (binding.dropdownMenuTransaction.editText as? AutoCompleteTextView)?.apply {
                setText(categoryType.label, false)
            }
        }
    }


    private fun updateSelectedAccount(account: Account) {
        binding.apply {
            textViewAccountName.text = account.username.standardize()
        }
    }

    private fun updateSelectedEvents(events: List<Event>) {
        binding.apply {
            customViewEvent.getTextView().isVisible = events.isEmpty()
            customViewEvent.getChipGroup().removeAllViews()

            // Add chips for each selected event (filter out nulls for safety)
            events.filterNotNull().forEach { event ->
                val chip = com.google.android.material.chip.Chip(requireContext())
                chip.text = event.eventName.standardize()
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    viewModel.removeEvent(event)
                }
                // Insert before the "Add Event" chip
                customViewEvent.getChipGroup()
                    .addView(chip, customViewEvent.getChipGroup().childCount - 1)
            }
        }
    }

    private fun updateSelectedPayees(payees: List<transaction.model.PayeeTransaction>) {
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

    private fun updateSelectedLocation(location: transaction.model.Location?) {
        binding.apply {
            customViewLocation.getTextView().isVisible = location == null
            customViewLocation.getChipGroup().removeAllViews()

            location?.let { loc ->
                val chip = com.google.android.material.chip.Chip(requireContext())
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

    fun toggleRecentlyCategory() {
        binding.apply {
            recyclerViewCategories.isVisible = !recyclerViewCategories.isVisible
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
            }
        )
        permissionHandler.setup(permissionLauncher)
    }

    private fun setupCameraHandler() {
        cameraHandler = CameraHandler(
            fragment = this,
            fileProvider = fileProvider,
            onImageCaptured = { uri ->
                viewModel.saveImage(uri)
            },
            onError = { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
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
            selectedDateStartMillis = localDate.atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli()

            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)
            selectedTimeOffsetMillis = (hour * 3_600_000L + minute * 60_000L)
        }
    }

    private fun setupCalculator() {
        // Get calculator view from activity that implements CalculatorProvider
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
}
