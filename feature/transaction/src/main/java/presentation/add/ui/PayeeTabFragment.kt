package presentation.add.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.R as TransactionR
import com.example.transaction.databinding.FragmentEventTabBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import presentation.add.adapter.PayeeAdapter
import presentation.add.model.PayeeTabType
import presentation.add.viewModel.AddTransactionViewModel
import presentation.add.viewModel.PayeeSelectViewModel
import transaction.model.PayeeTransaction

@AndroidEntryPoint
class PayeeTabFragment : BaseFragment<FragmentEventTabBinding>(
    FragmentEventTabBinding::inflate
) {
    private val viewModel: PayeeSelectViewModel by viewModels()
    private val addTransactionViewModel: AddTransactionViewModel by hiltNavGraphViewModels(TransactionR.id.transaction_nav_graph)
    private lateinit var adapter: PayeeAdapter
    private var tabType: PayeeTabType = PayeeTabType.RECENT
    private val selectedPayeeIds: Set<Long>
        get() = (parentFragment as? PayeeSelectFragment)?.getSelectedPayeeIds() ?: emptySet()

    override fun initView() {
        adapter = PayeeAdapter(
            { payee ->
                (parentFragment as PayeeSelectFragment).onPayeeToggled(payee.id)
            },
            {
                // TODO: Handle item update
            }
        )
        binding.recyclerView.adapter = adapter

        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? PayeeTabType
        tabType = tabTypeArg ?: PayeeTabType.RECENT

        if (tabType == PayeeTabType.RECENT) {
            viewModel.loadRecentPayees()
        } else {
            ensureContactsPermissionAndLoad()
        }
    }

    private fun ensureContactsPermissionAndLoad() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.getAllPhoneContacts()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                showPermissionRationale()
            }

            else -> {
                requestReadContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Contacts permission required")
            .setMessage("We need access to your contacts to show them in a list.")
            .setPositiveButton("Allow") { _, _ ->
                requestReadContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private val requestReadContactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.getAllPhoneContacts()
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun initListener() {
        binding.buttonAddTrip.setOnClickListener {
            showAddPayeeView()
        }

        binding.buttonSave.setOnClickListener {
            val payeeName = binding.editTextEventName.text.toString()
            if (payeeName.isBlank()) {
                return@setOnClickListener
            }
            
            // Create temporary payee and add to AddTransactionViewModel
            val payee = PayeeTransaction(
                id = 0, // Will be assigned temporary ID
                name = payeeName,
                accountId = 1L, // TODO: Get from account repository
                isFromContacts = false,
                contactId = null
            )
            addTransactionViewModel.addTemporaryPayee(payee)
            
            // Clear input
            binding.editTextEventName.text?.clear()
            showRecyclerView()
        }
    }

    override fun observeData() {
        // Observe persisted payees from PayeeSelectViewModel and merge with temporary ones
        viewLifecycleOwner.lifecycleScope.launch {
            combine(
                viewModel.uiState,
                addTransactionViewModel.temporaryPayees
            ) { persistedState, temporaryPayees ->
                when (persistedState) {
                    is UIState.Success -> {
                        // Merge persisted and temporary payees
                        val mergedPayees = temporaryPayees + persistedState.data
                        UIState.Success(mergedPayees)
                    }
                    is UIState.Loading -> UIState.Loading
                    is UIState.Error -> persistedState
                    else -> UIState.Idle
                }
            }.collect { mergedState ->
                when (mergedState) {
                    is UIState.Loading -> {}
                    is UIState.Success -> {
                        if (mergedState.data.isEmpty()) {
                            showEmptyView()
                        } else {
                            showRecyclerView()
                            adapter.submitList(mergedState.data)
                            // Update selection based on parent fragment's selection
                            val currentSelectedIds = selectedPayeeIds
                            if (currentSelectedIds.isNotEmpty()) {
                                val selectedPayees =
                                    mergedState.data.filter { currentSelectedIds.contains(it.id) }
                                adapter.setSelectedPayees(selectedPayees)
                            } else {
                                adapter.setSelectedPayees(emptyList())
                            }
                        }
                    }
                    else -> {
                        showEmptyView()
                    }
                }
            }
        }
    }

    fun showEmptyView() {
        binding.layoutEmpty.isVisible = true
        binding.recyclerView.isVisible = false
        binding.layoutAddEvent.isVisible = false
        binding.buttonAddTrip.isVisible = tabType == PayeeTabType.RECENT
    }

    fun showRecyclerView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = true
        binding.layoutAddEvent.isVisible = false
    }

    fun showAddPayeeView() {
        binding.layoutEmpty.isVisible = false
        binding.recyclerView.isVisible = false
        binding.layoutAddEvent.isVisible = true
    }

    fun refreshSelection() {
        // Refresh selection when parent fragment's selection changes
        val currentList = adapter.currentList
        if (currentList.isNotEmpty()) {
            val currentSelectedIds = selectedPayeeIds
            val selectedPayees = currentList.filter { currentSelectedIds.contains(it.id) }
            adapter.setSelectedPayees(selectedPayees)
        }
    }

    companion object {
        private const val ARG_TAB_TYPE = "payee_tab_type"
        const val ARG_SELECTED_PAYEE_IDS = "selected_payee_ids"

        fun newInstance(tabType: PayeeTabType): PayeeTabFragment {
            return PayeeTabFragment().apply {
                arguments = android.os.Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }
}
