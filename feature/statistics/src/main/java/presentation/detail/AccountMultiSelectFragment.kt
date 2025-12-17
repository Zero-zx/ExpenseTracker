package presentation.detail

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.statistics.databinding.FragmentAccountMultiSelectBinding
import constants.FragmentResultKeys.REQUEST_SELECT_ACCOUNT_IDS
import constants.FragmentResultKeys.RESULT_ACCOUNT_IDS
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.adapter.AccountMultiSelectAdapter
import ui.navigateBack
import ui.setSelectionResult

@AndroidEntryPoint
class AccountMultiSelectFragment : BaseFragment<FragmentAccountMultiSelectBinding>(
    FragmentAccountMultiSelectBinding::inflate
) {
    private val viewModel: AccountMultiSelectViewModel by viewModels()
    private val selectedAccountIds: MutableSet<Long> = mutableSetOf()
    
    private lateinit var adapter: AccountMultiSelectAdapter

    override fun initView() {
        // Get initially selected account IDs from arguments
        arguments?.getLongArray("selected_account_ids")?.let {
            selectedAccountIds.addAll(it.toList())
        }

        // Initialize adapter
        adapter = AccountMultiSelectAdapter(
            onAccountToggle = { accountId ->
                if (selectedAccountIds.contains(accountId)) {
                    selectedAccountIds.remove(accountId)
                } else {
                    selectedAccountIds.add(accountId)
                }
                refreshAdapter()
                updateSelectAllState()
            },
            selectedAccountIds = { selectedAccountIds }
        )

        binding.recyclerViewAccounts.adapter = adapter
        binding.recyclerViewAccounts.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        
        binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Select all accounts - will be handled in observeData
                viewModel.selectAllAccounts()
            } else {
                // Deselect all
                selectedAccountIds.clear()
                refreshAdapter()
                updateSelectAllState()
            }
        }

        viewModel.loadAccounts()
    }

    private fun refreshAdapter() {
        val currentList = adapter.currentList
        if (currentList.isNotEmpty()) {
            adapter.submitList(currentList)
        }
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            navigateBack()
        }

        binding.buttonConfirm.setOnClickListener {
            onConfirmSelection()
        }

        binding.editTextSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                true
            } else false
        }

        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            adapter.filter(text.toString())
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {
                    // Show loading if needed
                }
                is UIState.Success -> {
                    adapter.submitList(state.data)
                    updateSelectAllState()
                }
                else -> {}
            }
        }

        // Observe all account IDs for select all functionality
        viewModel.allAccountIds.observe(viewLifecycleOwner) { allIds ->
            if (binding.checkboxSelectAll.isChecked && allIds.isNotEmpty()) {
                selectedAccountIds.clear()
                selectedAccountIds.addAll(allIds)
                refreshAdapter()
            }
            updateSelectAllState()
        }
    }

    private fun updateSelectAllState() {
        viewModel.allAccountIds.observe(viewLifecycleOwner) { allIds ->
            val allSelected = allIds.isNotEmpty() && selectedAccountIds.containsAll(allIds)
            binding.checkboxSelectAll.setOnCheckedChangeListener(null)
            binding.checkboxSelectAll.isChecked = allSelected
            binding.checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedAccountIds.clear()
                    selectedAccountIds.addAll(allIds)
                    refreshAdapter()
                    updateSelectAllState()
                } else {
                    selectedAccountIds.clear()
                    refreshAdapter()
                    updateSelectAllState()
                }
            }
            
            val selectedCount = selectedAccountIds.size
            binding.textViewSelectedCount.text = "$selectedCount selected"
        }
    }

    private fun onConfirmSelection() {
        setSelectionResult(
            REQUEST_SELECT_ACCOUNT_IDS,
            bundleOf(RESULT_ACCOUNT_IDS to selectedAccountIds.toLongArray())
        )
        navigateBack()
    }

    companion object {
        const val ARG_SELECTED_ACCOUNT_IDS = "selected_account_ids"

        fun newInstance(
            selectedAccountIds: LongArray? = null
        ): AccountMultiSelectFragment {
            return AccountMultiSelectFragment().apply {
                arguments = android.os.Bundle().apply {
                    selectedAccountIds?.let {
                        putLongArray(ARG_SELECTED_ACCOUNT_IDS, it)
                    }
                }
            }
        }
    }
}

