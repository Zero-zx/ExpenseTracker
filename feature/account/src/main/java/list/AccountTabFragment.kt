package list

import account.model.Account
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.text.HtmlCompat.fromHtml
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.common.R
import com.example.login.databinding.FragmentTabAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import model.AccountTabType
import ui.gone
import ui.showDeleteConfirmation
import ui.showNotImplementToast
import ui.visible

@AndroidEntryPoint
class AccountTabFragment : BaseFragment<FragmentTabAccountBinding>(
    FragmentTabAccountBinding::inflate
) {
    private val parentAccountFragment: AccountListFragment?
        get() = parentFragment as? AccountListFragment
    private val viewModel: AccountListViewModel by viewModels()

    private lateinit var adapter: AccountListAdapter
    private val accountTabType: AccountTabType by lazy {
        val typeOrdinal = arguments?.getInt(ARG_ACCOUNT_TAB_TYPE, AccountTabType.ACCOUNT.ordinal)
            ?: AccountTabType.ACCOUNT.ordinal
        AccountTabType.entries[typeOrdinal]
    }

    override fun initView() {
        adapter = AccountListAdapter(
            onItemClick = { account ->
            },
            onMoreClick = { account ->
                showMenuBottomSheet(account)
            }
        )

        setUpTabView()

        setupRecyclerView()
    }

    override fun initListener() {
        binding.apply {
            buttonAddAccount.setOnClickListener {
                when (accountTabType) {
                    AccountTabType.ACCOUNT -> viewModel.navigateToAddAccount()
                    AccountTabType.SAVINGS -> showNotImplementToast()
                    AccountTabType.ACCUMULATE -> showNotImplementToast()
                }
            }
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> showLoading()
                is UIState.Idle -> showEmpty()
                is UIState.Success -> {
                    if (accountTabType == AccountTabType.ACCOUNT) {
                        showAccounts(state.data)
                    } else {
                        showEmpty()
                    }
                }

                is UIState.Error -> showError(state.message)
            }
        }
    }

    private fun setUpTabView() {
        when (accountTabType) {
            AccountTabType.ACCOUNT -> {
            }

            AccountTabType.SAVINGS -> {
                binding.apply {
                    imageViewEmpty.setImageResource(R.drawable.ic_have_not_saving_light)
                    textViewEmpty.text = getString(R.string.text_saving_account_empty)
                    textViewAddAccount.text = getString(R.string.text_add_saving_account)
                }
            }

            AccountTabType.ACCUMULATE -> {
                binding.apply {
                    imageViewEmpty.setImageResource(R.drawable.ic_have_not_goal_save_account_light)
                    textViewEmpty.text = fromHtml(
                        getString(R.string.text_saving_goal_text),
                        FROM_HTML_MODE_LEGACY
                    )
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewAccounts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AccountTabFragment.adapter
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visible()
            layoutEmpty.visible()
            layoutError.gone()
        }
    }

    private fun showEmpty() {
        binding.apply {
            layoutTotalAmount.gone()
            progressBar.gone()
            recyclerViewAccounts.gone()
            layoutEmpty.visible()
            layoutError.gone()
        }
    }

    private fun showAccounts(accounts: List<Account>) {
        binding.apply {
            progressBar.gone()
            recyclerViewAccounts.visible()
            layoutEmpty.gone()
            layoutError.gone()
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

    fun showMenuBottomSheet(account: Account) {
        val bottomSheet = AccountMenuBottomSheet(
            onTransferAccount = {
                showNotImplementToast()
            },
            onAdjustAccount = {
                showNotImplementToast()
            },
            onShareAccount = {
                showNotImplementToast()
            },
            onEditAccount = {
                showNotImplementToast()
            },
            onDeleteAccount = {
                showDeleteConfirmation(
                    itemName = getString(R.string.text_account_lower),
                    message = fromHtml(
                        getString(R.string.text_delete_account_warning),
                        FROM_HTML_MODE_LEGACY
                    ),
                    onDelete = {
                        viewModel.deleteAccount(account)
                    }
                )
            },
            onInactiveAccount = {
                showNotImplementToast()
            }
        )
        bottomSheet.show(parentFragmentManager, "AccountMenuBottomSheet")
    }

    companion object {
        private const val ARG_ACCOUNT_TAB_TYPE = "account_tab_type"

        fun newInstance(accountTabType: AccountTabType): AccountTabFragment {
            return AccountTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ACCOUNT_TAB_TYPE, accountTabType.ordinal)
                }
            }
        }
    }
}

