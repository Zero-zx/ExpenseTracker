package presentation.add.ui

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import base.BaseFragment
import base.UIState
import com.example.transaction.R
import com.example.transaction.databinding.FragmentTransactionAddBinding
import custom.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import data.model.Category
import data.model.CategoryType
import presentation.CategoryUiState
import presentation.add.adapter.CategoryAdapter
import presentation.add.viewModel.AddTransactionViewModel


@AndroidEntryPoint
class TransactionAddFragment : BaseFragment<FragmentTransactionAddBinding>(
    FragmentTransactionAddBinding::inflate
) {
    private val viewModel: AddTransactionViewModel by navGraphViewModels(R.id.transaction_graph) { defaultViewModelProviderFactory }
    private val adapter = CategoryAdapter(
        onItemClick = { category ->
            viewModel.selectCategory(category = category)
        }
    )

    override fun initView() {
        val items = CategoryType.entries
        val dropdownAdapter = ArrayAdapter(requireContext(), R.layout.menu_item, items)
        (binding.dropdownMenuTransaction.editText as? AutoCompleteTextView)?.setAdapter(
            dropdownAdapter
        )

        binding.recyclerViewCategories.apply {
            layoutManager = GridLayoutManager(context, 4)
            addItemDecoration(GridSpacingItemDecoration(4, 8, false))
        }.adapter = adapter
    }


    override fun initListener() {
        binding.buttonListTransaction.setOnClickListener {
            viewModel.onHistoryClick()
        }

        binding.layoutCategorySelection.setOnClickListener {
            viewModel.toSelectCategory()
        }

        binding.layoutRecentlyUse.setOnClickListener {
            toggleRecentlyCategory()
        }

        binding.buttonSelectWallet.setOnClickListener {
            viewModel.toSelectAccount()
        }


        binding.buttonSubmit.setOnClickListener {
            val selectedCategory = viewModel.selectedCategory.value
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

    override fun observeData() {
        collectState(viewModel.categoryState) { state ->
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
            }
        }

        collectState(viewModel.selectedCategory) { category ->
            // Update selection highlight in the categories RecyclerView and get the adapter position
            adapter.setSelectedCategory(category)
            category?.let { updateCategoryUI(it) }
        }

        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {}
                is UIState.Success -> {
                    Toast.makeText(
                        context,
                        "Transaction added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UIState.Error -> {
                    Toast.makeText(
                        context,
                        "Error adding transaction: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }
    }

    private fun updateCategoryUI(category: Category) {
        binding.apply {
            // Update icon
            iconCategory.imageIcon.setImageResource(category.iconRes)
            // Update category name
            textViewCategory.text = category.title
        }
    }

    fun toggleRecentlyCategory() {
        binding.recyclerViewCategories.isVisible = !binding.recyclerViewCategories.isVisible
    }

    private fun showCategories(categories: List<Category>) {
        adapter.submitList(categories)
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetTransactionState()
    }
}
