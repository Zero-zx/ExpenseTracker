package presentation.add.ui

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentAccountSelectBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.AccountAdapter
import presentation.add.viewModel.AccountSelectViewModel
import ui.setAccountIdSelectionResult

@AndroidEntryPoint
class AccountSelectFragment : BaseFragment<FragmentAccountSelectBinding>(
    FragmentAccountSelectBinding::inflate
) {
    private val viewModel: AccountSelectViewModel by viewModels()
    private lateinit var adapter: AccountAdapter
    private var searchVisible = false

    // Get the currently selected account ID from arguments
    private val selectedAccountId: Long by lazy {
        arguments?.getLong(ARG_SELECTED_ACCOUNT_ID, -1L) ?: -1L
    }

    override fun initView() {
        adapter = AccountAdapter { account ->
            setAccountIdSelectionResult(account.id)
            viewModel.navigateBack()
        }
        binding.recyclerView.adapter = adapter
    }

    companion object {
        const val ARG_SELECTED_ACCOUNT_ID = "selected_account_id"
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            viewModel.navigateBack()
        }

        binding.buttonSearch.setOnClickListener {
            toggleSearch(!searchVisible)
        }

        binding.editTextSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                // handle search action (e.g. filter adapter)
                hideKeyboard(v)
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
                is UIState.Loading -> {}
                is UIState.Success -> {
                    adapter.submitList(state.data)

                    // Highlight the currently selected account if ID is valid
                    if (selectedAccountId != -1L) {
                        val selectedAccount = state.data.find { it.id == selectedAccountId }
                        adapter.setSelectedAccount(selectedAccount)
                    }
                }

                else -> {}
            }
        }
    }

    private fun toggleSearch(show: Boolean) {
        searchVisible = show
        if (show) {
            binding.textViewTitle.visibility = View.GONE
            binding.editTextSearch.visibility = View.VISIBLE
            binding.editTextSearch.requestFocus()
            showKeyboard(binding.editTextSearch)
        } else {
            binding.editTextSearch.setText("")
            binding.editTextSearch.visibility = View.GONE
            binding.textViewTitle.visibility = View.VISIBLE
            hideKeyboard(binding.editTextSearch)
        }
    }

    private fun showKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.post {
            view.requestFocus()
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
