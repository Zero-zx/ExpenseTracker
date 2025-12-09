package presentation.add.ui

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentEventTabBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.PayeeAdapter
import presentation.add.model.PayeeTabType
import presentation.add.viewModel.PayeeSelectViewModel

@AndroidEntryPoint
class PayeeTabFragment : BaseFragment<FragmentEventTabBinding>(
    FragmentEventTabBinding::inflate
) {
    private val viewModel: PayeeSelectViewModel by viewModels()
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

        // Load appropriate data based on tab type
        if (tabType == PayeeTabType.RECENT) {
            viewModel.loadRecentPayees()
        } else {
            viewModel.loadPayees() // For contacts tab (to be implemented later)
        }
    }

    override fun initListener() {
        binding.buttonAddTrip.setOnClickListener {
            showAddPayeeView()
        }

        binding.buttonSave.setOnClickListener {
            val payeeName = binding.editTextEventName.text.toString()
            viewModel.addPayee(payeeName)
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {
                    // Clear the input field after successful add
                    binding.editTextEventName.text?.clear()

                    if (state.data.isEmpty()) {
                        showEmptyView()
                    } else {
                        showRecyclerView()
                        adapter.submitList(state.data)
                        // Update selection based on parent fragment's selection
                        val currentSelectedIds = selectedPayeeIds
                        if (currentSelectedIds.isNotEmpty()) {
                            val selectedPayees = state.data.filter { currentSelectedIds.contains(it.id) }
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
