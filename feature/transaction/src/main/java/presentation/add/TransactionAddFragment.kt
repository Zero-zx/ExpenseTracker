package presentation.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
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
import presentation.AddTransactionUiState
import presentation.CategoryUiState


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
        setupFragmentResultListener()
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

        // Click listener for category selection area
        binding.layoutCategorySelection.setOnClickListener {
            viewModel.onMoreCategory()
        }

        binding.buttonSubmit.setOnClickListener {
            val selectedCategory = viewModel.transactionState.value.selectedCategory
            if (selectedCategory == null) {
                Toast.makeText(
                    context,
                    "Please select a category",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            
            viewModel.addTransaction(
                amount = binding.editTextAmount.text.toString().toDoubleOrNull() ?: 0.0,
                description = binding.editTextAmount.text?.toString()
            )
        }
    }

    private fun setupFragmentResultListener() {
        setFragmentResultListener(CategorySelectFragment.REQUEST_KEY) { _, bundle ->
            val categoryId = bundle.getLong(CategorySelectFragment.RESULT_KEY, -1L)
            if (categoryId != -1L) {
                // Find category from current state
                val currentState = viewModel.categoryState.value
                if (currentState is CategoryUiState.Success) {
                    val category = currentState.categories.find { it.id == categoryId }
                    category?.let {
                        viewModel.selectCategory(it)
                        updateCategoryUI(it)
                    }
                }
            }
        }
    }

    private fun updateCategoryUI(category: Category) {
        binding.apply {
            // Update icon
            iconCategory.imageIcon.setImageResource(category.icon)
            // Update category name
            textViewCategory.text = category.title
        }
    }

    private fun observeCategoryState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryState.collect { state ->
                    when (state) {
                        is CategoryUiState.Loading -> {
                            // Show loading indicator if needed
                        }
                        is CategoryUiState.Success -> {
                            showCategories(state.categories.take(8))
                        }
                        is CategoryUiState.Error -> {
                            Toast.makeText(
                                context,
                                "Error loading categories: ${state.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun observeAddTransactionSate() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactionState.collect { state ->
                    // Update UI when category is selected
                    state.selectedCategory?.let { category ->
                        updateCategoryUI(category)
                    }
                    
                    when (state) {
                        is AddTransactionUiState.Initial -> {
                            // Initial state, no action needed
                        }
                        is AddTransactionUiState.Loading -> {
                            // Show loading indicator if needed
                        }
                        is AddTransactionUiState.Success -> {
                            Toast.makeText(
                                context,
                                "Transaction added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is AddTransactionUiState.Error -> {
                            Toast.makeText(
                                context,
                                "Error adding transaction: ${state.message}",
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
