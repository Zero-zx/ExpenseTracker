package presentation.add.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentBorrowerTabBinding
import dagger.hilt.android.AndroidEntryPoint
import payee.model.Payee
import payee.model.PayeeType
import presentation.add.adapter.PayeeAdapter
import presentation.add.model.PayeeTabType
import presentation.add.viewModel.PayeeSelectViewModel
import ui.CustomAlertDialog
import ui.showEditDialog

@AndroidEntryPoint
class BorrowerTabFragment : BaseFragment<FragmentBorrowerTabBinding>(
    FragmentBorrowerTabBinding::inflate
) {
    private val viewModel: PayeeSelectViewModel by viewModels()
    private lateinit var adapter: PayeeAdapter
    private var tabType: PayeeTabType = PayeeTabType.RECENT
    private val selectedBorrowerName: String by lazy {
        parentFragment?.arguments?.getString(ARG_SELECTED_BORROWER_NAME, "") ?: ""
    }

    override fun initView() {
        adapter = PayeeAdapter(onItemClick = { borrower ->
            (parentFragment as BorrowerSelectFragment).onBorrowerSelected(borrower.name)
        }, onItemUpdate = { borrower ->
            handleBorrowerEdit(borrower)
        })
        binding.recyclerView.adapter = adapter

        val tabTypeArg = arguments?.getSerializable(ARG_TAB_TYPE) as? PayeeTabType
        tabType = tabTypeArg ?: PayeeTabType.RECENT

        showAddBorrowerViewWithData(selectedBorrowerName)
        viewModel.updatePayeeType(PayeeType.BORROWER)
    }

    override fun initListener() {
        binding.buttonAddBorrower.setOnClickListener {
            showListView()
        }

        binding.buttonSave.setOnClickListener {
            val borrowerName = binding.editTextBorrowerName.text.toString()
            if (borrowerName.isBlank()) {
                return@setOnClickListener
            }

            (parentFragment as BorrowerSelectFragment).onBorrowerSelected(borrowerName)
        }

        binding.editTextBorrowerName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSearchQuery(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun observeData() {
        // Observe borrower list from database
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> handleSuccessState(state.data)
                is UIState.Error -> handleErrorState()
                is UIState.Idle -> handleEmptyState()
            }
        }
    }

    private fun handleSuccessState(borrowers: List<Payee>) {
        handleBorrowerListLoaded(borrowers)
    }

    private fun handleErrorState() {
        showEmptyView()
    }

    private fun handleEmptyState() {
        if (binding.editTextBorrowerName.text.isNullOrBlank()) showEmptyView()
    }


    private fun handleBorrowerListLoaded(borrowers: List<Payee>) {
        showListView()
        adapter.submitList(borrowers)
    }


    private fun handleBorrowerEdit(borrower: Payee) {
        showEditDialog(
            title = "Edit borrower",
            inputHint = "Enter borrower name",
            name = borrower.name,
            onUpdate = { name, _ ->
                viewModel.updatePayee(
                    borrower.copy(
                        name = name
                    )
                )
            },
            onDelete = {
                showDeleteConfirmation(borrower)
            })
    }

    private fun showDeleteConfirmation(borrower: Payee) {
        CustomAlertDialog.Builder(requireContext()).setTitle("Delete borrower")
            .setMessage("Are you sure you want to delete '${borrower.name}'?")
            .setPositiveButton("Delete") { dialog ->
                viewModel.deletePayee(borrower.id)
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog ->
                dialog.dismiss()
            }.show()
    }

    private fun showAddBorrowerViewWithData(borrowerName: String) {
        showListView()
        binding.editTextBorrowerName.setText(borrowerName)
    }

    fun showEmptyView() {
        binding.layoutEmpty.isVisible = true
        binding.layoutAddBorrower.isVisible = false
        binding.buttonAddBorrower.isVisible = tabType == PayeeTabType.RECENT
    }


    fun showListView() {
        binding.layoutEmpty.isVisible = false
        binding.layoutAddBorrower.isVisible = true
    }

    companion object {
        private const val ARG_TAB_TYPE = "borrower_tab_type"
        const val ARG_SELECTED_BORROWER_NAME = "selected_borrower_name"

        fun newInstance(tabType: PayeeTabType): BorrowerTabFragment {
            return BorrowerTabFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TAB_TYPE, tabType)
                }
            }
        }
    }
}