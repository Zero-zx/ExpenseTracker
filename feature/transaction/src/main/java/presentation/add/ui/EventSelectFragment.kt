package presentation.add.ui

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.R
import com.example.transaction.databinding.FragmentEventSelectBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.EventAdapter
import presentation.add.viewModel.AddTransactionViewModel
import presentation.add.viewModel.EventSelectViewModel

@AndroidEntryPoint
class EventSelectFragment : BaseFragment<FragmentEventSelectBinding>(
    FragmentEventSelectBinding::inflate
) {
    private val sharedViewModel: AddTransactionViewModel by navGraphViewModels(R.id.transaction_graph) { defaultViewModelProviderFactory }
    private val viewModel: EventSelectViewModel by viewModels()
    private lateinit var adapter: EventAdapter
    private var searchVisible = false

    override fun initView() {
        adapter = EventAdapter(
            { event ->
                // Set selected account on the shared nav-graph scoped ViewModel and navigate back
                sharedViewModel.selectEvent(event)
                sharedViewModel.navigateBack()
            },
            {
                // TODO: Handle item update
            }
        )
        binding.recyclerView.adapter = adapter
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            sharedViewModel.navigateBack()
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
            // react to search input (filter list)
        }
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {
                    adapter.submitList(state.data)
                }

                else -> {}
            }
        }

        collectState(sharedViewModel.selectedEvent) { event ->
            adapter.setSelectedEvent(event)
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
