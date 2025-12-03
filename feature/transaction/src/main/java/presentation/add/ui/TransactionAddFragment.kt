package presentation.add.ui

import account.model.Account
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

            customTextEvent.setOnClickListener {
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
        collectState(viewModel.selectedEvent) { event ->
            event?.let { updateSelectedEvent(it) }
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
            iconCategory.imageIcon.setImageResource(category.iconRes)
            // Update category name
            textViewCategory.text = category.title.standardize()
        }
    }


    private fun updateSelectedAccount(account: Account) {
        binding.apply {
            textViewAccountName.text = account.username.standardize()
        }
    }

    private fun updateSelectedEvent(event: Event) {
        binding.apply {
            customTextEvent.setText(event.eventName.standardize())
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
