package presentation.add.ui

import account.model.Account
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import base.BaseFragment
import base.UIState
import com.example.transaction.R
import com.example.transaction.databinding.FragmentTransactionAddBinding
import constants.FragmentResultKeys.REQUEST_SELECT_ACCOUNT_ID
import constants.FragmentResultKeys.REQUEST_SELECT_CATEGORY_ID
import constants.FragmentResultKeys.REQUEST_SELECT_EVENT_ID
import constants.FragmentResultKeys.REQUEST_SELECT_PAYEE_IDS
import constants.FragmentResultKeys.REQUEST_SELECT_LOCATION_ID
import constants.FragmentResultKeys.RESULT_ACCOUNT_ID
import constants.FragmentResultKeys.RESULT_CATEGORY_ID
import constants.FragmentResultKeys.RESULT_EVENT_ID
import constants.FragmentResultKeys.RESULT_PAYEE_IDS
import constants.FragmentResultKeys.RESULT_LOCATION_ID
import dagger.hilt.android.AndroidEntryPoint
import helpers.standardize
import presentation.add.adapter.CategoryAdapter
import presentation.add.viewModel.AddTransactionViewModel
import transaction.model.Category
import transaction.model.CategoryType
import transaction.model.Event
import ui.GridSpacingItemDecoration
import ui.createSlideDownAnimation
import ui.createSlideUpAnimation
import ui.listenForSelectionResult
import ui.openDatePicker
import ui.openTimePicker


@AndroidEntryPoint
class TransactionAddFragment : BaseFragment<FragmentTransactionAddBinding>(
    FragmentTransactionAddBinding::inflate
) {
    private val viewModel: AddTransactionViewModel by viewModels()
    private val adapter = CategoryAdapter(
        onItemClick = { category ->
            viewModel.selectCategory(category = category)
        }
    )

    // store selected date (start-of-day millis) and time (offset millis from midnight)
    private var selectedDateStartMillis: Long? = null
    private var selectedTimeOffsetMillis: Long? = null

    override fun initView() {
        val items = CategoryType.entries
        val dropdownAdapter = ArrayAdapter(requireContext(), R.layout.menu_item, items)
        (binding.dropdownMenuTransaction.editText as? AutoCompleteTextView)?.setAdapter(
            dropdownAdapter
        )

        setUpRecyclerView()
        listenForResult()
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

                viewModel.addTransaction(
                    amount = editTextAmount.text.toString().toDoubleOrNull() ?: 0.0,
                    description = editTextAmount.text?.toString(),
                    createAt = selectedDateStartMillis?.plus(selectedTimeOffsetMillis ?: 0)
                        ?: System.currentTimeMillis()
                )
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
            category?.let { updateSelectedCategory(it) }
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

        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {
                    Toast.makeText(
                        context,
                        "Transaction added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UIState.Error -> {
                    Toast.makeText(
                        context,
                        "Error adding transaction: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
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

    override fun onResume() {
        super.onResume()
        viewModel.resetTransactionState()
    }
}
