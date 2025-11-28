package presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login.databinding.FragmentAccountListBinding
import com.example.login.R
import dagger.hilt.android.AndroidEntryPoint
import data.model.Account
import kotlinx.coroutines.launch
import presentation.AccountListUiState

@AndroidEntryPoint
class AccountListFragment : Fragment() {

    private var _binding: FragmentAccountListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountListViewModel by viewModels()
    private val adapter = AccountListAdapter(
        onItemClick = { account ->
            // Navigate to detail screen if needed
            // findNavController().navigate(...)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        observeUiState()
    }

    private fun setupFab() {
        binding.fabAddAccount.setOnClickListener {
            viewModel.goToAddAccount()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewAccounts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AccountListFragment.adapter
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AccountListUiState.Loading -> showLoading()
                        is AccountListUiState.Empty -> showEmpty()
                        is AccountListUiState.Success -> showAccounts(state.accounts)
                        is AccountListUiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerViewAccounts.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

