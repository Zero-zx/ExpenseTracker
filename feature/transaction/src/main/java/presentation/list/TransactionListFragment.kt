package presentation.list

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentTransactionListBinding
import dagger.hilt.android.AndroidEntryPoint
import navigation.Navigator
import transaction.model.Transaction
import javax.inject.Inject
import kotlinx.coroutines.launch

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

    override fun initView() {
        setupRecyclerView()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

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
