package presentation.list

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentTransactionListBinding
import constants.FragmentResultKeys
import dagger.hilt.android.AndroidEntryPoint
import navigation.Navigator
import ui.listenForSelectionResult
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TransactionListFragment : BaseFragment<FragmentTransactionListBinding>(
    FragmentTransactionListBinding::inflate
) {
    private val viewModel: TransactionListViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator

    private val adapter = TransactionListAdapter(
        onItemClick = { transaction ->
            // Navigate to edit transaction screen
            navigator.navigateToEditTransaction(transaction.id)
        }
    )

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun initView() {
        setupRecyclerView()
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            navigator.navigateUp()
        }

        binding.buttonSearch.setOnClickListener {
            // TODO: Implement search functionality
        }

        binding.buttonMenu.setOnClickListener {
            // TODO: Implement menu functionality
        }

        binding.layoutQuarterSelection.setOnClickListener {
            navigator.navigateToDataSetting()
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
            headerLayout.visibility = View.VISIBLE
            recyclerViewTransactions.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE

            // Update header
            textViewQuarter.text = data.selectedPeriod
            val formattedIncome = currencyFormatter.format(data.totalIncome).replace("$", "₫")
            val formattedExpense = currencyFormatter.format(data.totalExpense).replace("$", "₫")
            textViewTotalIncome.text = formattedIncome
            textViewTotalExpense.text = formattedExpense
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
}
