package presentation.add

import android.accounts.Account
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.transaction.R
import com.example.transaction.databinding.FragmentTransactionAddBinding
import constants.CategoryIcon
import dagger.hilt.android.AndroidEntryPoint
import data.model.Category
import data.model.CategoryType
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TransactionAddFragment : Fragment() {

    private var _binding: FragmentTransactionAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModels()
    private val adapter = CategoryAdapter(
        onItemClick = { category ->
            // Navigate to detail screen
            // findNavController().navigate(...)
            viewModel.selectCategory(category = category)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")
        val adapter = ArrayAdapter(requireContext(), R.layout.menu_item, items)
        (binding.dropdownMenuTransaction.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        setupRecyclerView()
        observeUiState()
        setUpListeners()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCategories.apply {
            adapter = this@TransactionAddFragment.adapter
        }
    }

    private fun observeUiState() {
        observeCategoryState()
        observeAddTransactionSate()
    }

    private fun setUpListeners() {
        binding.buttonListTransaction.setOnClickListener {
            viewModel.onHistoryClick()
        }

        binding.buttonSubmit.setOnClickListener {
            viewModel.addTransaction(
                amount = binding.editTextAmount.text.toString().toDouble(),
                description = "Description",
                category = Category(
                    id = 1,
                    parentId = null,
                    title = "Category",
                    icon = CategoryIcon.SALARY.iconRes,
                    type = CategoryType.IN
                ),
                account = Account(
                    "Account",
                    "AccountType"
                )
            )
        }
    }

    private fun observeCategoryState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryState.collect { state ->
                    when {
                        state.categories != null -> {
                            showCategories(state.categories.take(8))
                        }

                        state.error != null -> {
                            Toast.makeText(
                                context,
                                "Error loading categories: ${state.error}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun observeAddTransactionSate() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactionSate.collect { state ->
                    when {
                        state.transactionId != null -> {
                            Toast.makeText(
                                context,
                                "Transaction added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        state.error != null -> {
                            Toast.makeText(
                                context,
                                "Error adding transaction: ${state.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun showCategories(categories: List<Category>) {
        adapter.submitList(categories)
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetTransactionState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
