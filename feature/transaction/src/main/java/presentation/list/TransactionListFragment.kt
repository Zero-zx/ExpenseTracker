package presentation.list

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transaction.databinding.FragmentTransactionListBinding
import dagger.hilt.android.AndroidEntryPoint
import data.model.Transaction
import kotlinx.coroutines.launch
import presentation.TransactionListUiState
import ui.BaseFragment

@AndroidEntryPoint
class TransactionListFragment : BaseFragment<FragmentTransactionListBinding>(
    FragmentTransactionListBinding::inflate
) {
    private val viewModel: TransactionListViewModel by viewModels()
    private val adapter = TransactionListAdapter(
        onItemClick = { transaction ->
            // Navigate to detail screen
            // findNavController().navigate(...)
        }
    )

    override fun FragmentTransactionListBinding.initialize() {
        setupRecyclerView()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TransactionListFragment.adapter
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is TransactionListUiState.Loading -> showLoading()
                        is TransactionListUiState.Empty -> showEmpty()
                        is TransactionListUiState.Success -> showTransactions(state.transactions)
                        is TransactionListUiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerViewTransactions.visibility = View.GONE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewTransactions.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
            layoutError.visibility = View.GONE
        }
    }

    private fun showTransactions(transactions: List<Transaction>) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewTransactions.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE
        }
        adapter.submitList(transactions)
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
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
