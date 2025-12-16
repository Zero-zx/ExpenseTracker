package list

import account.model.Account
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.login.databinding.FragmentTabAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountTabFragment : BaseFragment<FragmentTabAccountBinding>(
    FragmentTabAccountBinding::inflate
) {

    private val viewModel: AccountListViewModel by viewModels()

    private lateinit var adapter: AccountListAdapter

    override fun initView() {
        // Initialize adapter here to avoid recursive type inference
        adapter = AccountListAdapter(
            onItemClick = { account ->
                // Select this account
                viewModel.selectAccount(account.id)
                adapter.updateSelectedAccount(account.id)
            }
        )

        setupRecyclerView()
        // Set initial selected account
        val currentAccountId = viewModel.getCurrentAccountId()
        adapter.updateSelectedAccount(currentAccountId)
    }

    override fun initListener() {
        binding.fabAddAccount.setOnClickListener {
            viewModel.goToAddAccount()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewAccounts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AccountTabFragment.adapter
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> showLoading()
                is UIState.Idle -> showEmpty()
                is UIState.Success -> showAccounts(state.data)
                is UIState.Error -> showError(state.message)
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewAccounts.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
            layoutError.visibility = View.GONE
        }
    }

    private fun showAccounts(accounts: List<Account>) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewAccounts.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.GONE
        }
        adapter.submitList(accounts)
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerViewAccounts.visibility = View.GONE
            layoutEmpty.visibility = View.GONE
            layoutError.visibility = View.VISIBLE
            textViewError.text = message
            buttonRetry.setOnClickListener {
                viewModel.refresh()
            }
        }
    }

}

