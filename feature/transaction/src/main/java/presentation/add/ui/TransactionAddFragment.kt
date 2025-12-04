package presentation.add.ui

import account.model.Account
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import base.BaseFragment
import base.UIState
import com.example.transaction.R
import com.example.transaction.databinding.FragmentTransactionAddBinding
import dagger.hilt.android.AndroidEntryPoint
import helpers.standardize
import presentation.CategoryUiState
import presentation.add.adapter.CategoryAdapter
import presentation.add.viewModel.AddTransactionViewModel
import transaction.model.Category
import transaction.model.CategoryType
import transaction.model.Event
import ui.GridSpacingItemDecoration
import ui.createSlideDownAnimation
import ui.createSlideUpAnimation
import ui.openDatePicker
import ui.openTimePicker


@AndroidEntryPoint
class TransactionAddFragment : BaseFragment<FragmentTransactionAddBinding>(
    FragmentTransactionAddBinding::inflate
) {
    private val viewModel: AddTransactionViewModel by navGraphViewModels(R.id.transaction_graph) { defaultViewModelProviderFactory }
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

        binding.recyclerViewCategories.apply {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(GridSpacingItemDecoration(4, 8, false))
        }.adapter = adapter
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

            layoutEventSelection.setOnClickListener {
                viewModel.toSelectEvent()
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
                is CategoryUiState.Loading -> {
                    // Show loading indicator if needed
                }

                is CategoryUiState.Success -> {
                    showCategories(state.categories.take(8))
                }

                is CategoryUiState.Error -> {
                    Toast.makeText(
                        context,
                        "Error loading categories: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        collectState(viewModel.selectedCategory) { category ->
            adapter.setSelectedCategory(category)
            category?.let { updateSelectedCategory(it) }
        }

        collectState(viewModel.selectedAccount) { account ->
            account?.let { updateSelectedAccount(it) }
        }

        collectState(viewModel.selectedEvents) { events ->
            updateSelectedEvents(events)
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
            textViewEventHint.isVisible = events.isEmpty()
            chipGroupEvents.removeAllViews()

            // Add chips for each selected event
            events.forEach { event ->
                val chip = com.google.android.material.chip.Chip(requireContext())
                chip.text = event.eventName.standardize()
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    viewModel.removeEvent(event)
                }
                // Insert before the "Add Event" chip
                chipGroupEvents.addView(chip, chipGroupEvents.childCount - 1)
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
