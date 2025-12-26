package presentation.list

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentTransactionListBinding
import constants.FragmentResultKeys
import dagger.hilt.android.AndroidEntryPoint
import helpers.formatAsCurrency
import ui.listenForSelectionResult
import ui.navigateBack
import java.text.NumberFormat
import java.util.Locale
import com.example.common.R as CommonR

@AndroidEntryPoint
class TransactionListFragment : BaseFragment<FragmentTransactionListBinding>(
    FragmentTransactionListBinding::inflate
) {
    private val viewModel: TransactionListViewModel by viewModels()


    private val adapter = TransactionListAdapter(
        onItemClick = { transaction ->
            if (!viewModel.isSelectionMode.value) {
                viewModel.navigateToEditTransaction(transaction.id)
            }
        },
        onItemSelect = { transaction ->
            viewModel.toggleTransactionSelection(transaction.id)
        }
    )


    override fun initView() {
        setupRecyclerView()
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            navigateBack()
        }

        binding.buttonSearch.setOnClickListener {
            // TODO: Implement search functionality
        }

        binding.buttonMenu.setOnClickListener {
            showMenuBottomSheet()
        }

        binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectAllTransactions()
            } else {
                viewModel.clearSelection()
            }
        }

        binding.buttonCancel.setOnClickListener {
            viewModel.exitSelectionMode()
        }

        binding.buttonDelete.setOnClickListener {
            viewModel.deleteSelectedTransactions()
        }

        binding.textViewDelete.setOnClickListener {
            viewModel.deleteSelectedTransactions()
        }

        binding.layoutTimeSelection.setOnClickListener {
            viewModel.navigateToDataSetting()
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> showLoading()
                is UIState.Idle -> showEmpty()
                is UIState.Success -> showTransactions(state.data)
                is UIState.Error -> showError(state.message)
            }
        }

        // Observe selection mode
        collectState(viewModel.isSelectionMode) { isSelectionMode ->
            updateSelectionModeUI(isSelectionMode)
        }

        // Observe selected transactions
        collectState(viewModel.selectedTransactions) { selectedIds ->
            updateSelectionUI(selectedIds)
        }

        // Listen for data setting result
        listenForSelectionResult(FragmentResultKeys.REQUEST_DATA_SETTING) { bundle ->
            if (bundle.containsKey(FragmentResultKeys.RESULT_START_DATE) &&
                bundle.containsKey(FragmentResultKeys.RESULT_END_DATE)
            ) {
                val startDate = bundle.getLong(FragmentResultKeys.RESULT_START_DATE)
                val endDate = bundle.getLong(FragmentResultKeys.RESULT_END_DATE)
                val periodLabel =
                    bundle.getString(FragmentResultKeys.RESULT_PERIOD_LABEL) ?: "Quarter IV"

                // Validate date range
                if (startDate > 0 && endDate >= startDate) {
                    viewModel.loadTransactionsForDateRange(startDate, endDate, periodLabel)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TransactionListFragment.adapter
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            headerLayout.visibility = View.GONE
            recyclerViewTransactions.visibility = View.GONE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            headerLayout.visibility = View.GONE
            recyclerViewTransactions.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
            layoutError.visibility = View.GONE
        }
    }

    private fun showTransactions(data: TransactionListData) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewTransactions.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE

            // Update header only if not in selection mode
            if (!viewModel.isSelectionMode.value) {
                headerLayout.visibility = View.VISIBLE
                textViewQuarter.text = data.selectedPeriod
                textViewTotalIncome.text = data.totalIncome.formatAsCurrency("₫")
                textViewTotalExpense.text = data.totalExpense.formatAsCurrency("₫")
            }

            // Update adapter selection state
            adapter.setSelectionMode(viewModel.isSelectionMode.value)
            adapter.setSelectedTransactions(viewModel.selectedTransactions.value)
        }
        adapter.submitList(data.items)
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            headerLayout.visibility = View.GONE
            recyclerViewTransactions.visibility = View.GONE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.VISIBLE
            textViewError.text = message
            buttonRetry.setOnClickListener {
                viewModel.refresh()
            }
        }
    }

    private fun showMenuBottomSheet() {
        val bottomSheet = TransactionMenuBottomSheet(
            onSelectTransaction = {
                viewModel.enterSelectionMode()
            },
            onDisplaySettings = {
                // TODO: Implement display settings
            },
            onFilterOption = {
                // TODO: Implement filter option
            }
        )
        bottomSheet.show(parentFragmentManager, "TransactionMenuBottomSheet")
    }

    private fun updateSelectionModeUI(isSelectionMode: Boolean) {
        binding.apply {
            if (isSelectionMode) {
                // Update toolbar
                if (viewModel.selectedTransactions.value.isEmpty()) {
                    textViewTitle.text = "Select transaction"
                    textViewDelete.setTextColor(CommonR.color.black_text)
                } else {
                    textViewTitle.text = "${viewModel.selectedTransactions.value.size} selected"
                    textViewDelete.setTextColor(CommonR.color.blue_bg)
                }

                buttonSearch.visibility = View.GONE
                buttonMenu.visibility = View.GONE
                textViewDelete.visibility = View.VISIBLE

                // Hide header layout
                layoutTimeSelection.visibility = View.GONE

                // Show action bar
                layoutActionBar.visibility = View.VISIBLE
            } else {
                // Restore normal UI
                textViewTitle.text = "Transaction history"
                buttonSearch.visibility = View.VISIBLE
                buttonMenu.visibility = View.VISIBLE
                textViewDelete.visibility = View.GONE

                // Show header layout
                headerLayout.visibility = View.VISIBLE

                // Hide action bar
                layoutActionBar.visibility = View.GONE
                checkboxSelectAll.isChecked = false
            }

            // Update adapter
            adapter.setSelectionMode(isSelectionMode)
        }
    }

    private fun updateSelectionUI(selectedIds: Set<Long>) {
        binding.apply {
            // Update title
            if (viewModel.isSelectionMode.value) {
                textViewTitle.text = if (selectedIds.isEmpty()) {
                    "Select transaction"
                } else {
                    "${selectedIds.size} selected"
                }
            }

            // Update select all checkbox
            val allTransactionIds = adapter.currentList.flatMap { item ->
                if (item is TransactionListItem.DateHeader) {
                    item.transactions.map { it.id }
                } else {
                    emptyList()
                }
            }
            checkboxSelectAll.isChecked = selectedIds.isNotEmpty() &&
                    selectedIds.containsAll(allTransactionIds)

            // Update adapter
            adapter.setSelectedTransactions(selectedIds)
        }
    }
}
