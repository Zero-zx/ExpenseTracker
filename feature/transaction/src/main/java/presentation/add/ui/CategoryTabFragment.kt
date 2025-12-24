package presentation.add.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import base.BaseFragment
import base.UIState
import category.model.CategoryType
import com.example.transaction.R
import com.example.transaction.databinding.FragmentCategoryTabBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.ExpandableCategoryAdapter
import presentation.add.adapter.MostUsingCategoryAdapter
import presentation.add.viewModel.AddTransactionViewModel
import presentation.add.viewModel.CategoryTabViewModel

@AndroidEntryPoint
class CategoryTabFragment : BaseFragment<FragmentCategoryTabBinding>(
    FragmentCategoryTabBinding::inflate
) {
    private val viewModel: CategoryTabViewModel by viewModels()
    private val addTransactionViewModel: AddTransactionViewModel by hiltNavGraphViewModels(R.id.transaction_nav_graph)

    private val adapter = ExpandableCategoryAdapter(
        onCategoryClick = { category ->
            // Notify parent fragment about category selection
            (parentFragment as? CategorySelectFragment)?.onCategorySelected(category.id)
                ?: addTransactionViewModel.selectCategory(category)
        }
    )

    private val mostUsingAdapter = MostUsingCategoryAdapter(
        onCategoryClick = { category ->
            // Notify parent fragment about category selection
            (parentFragment as? CategorySelectFragment)?.onCategorySelected(category.id)
                ?: addTransactionViewModel.selectCategory(category)
        }
    )

    // Get the category type from arguments
    private val categoryType: CategoryType by lazy {
        val typeOrdinal = arguments?.getInt(ARG_CATEGORY_TYPE, CategoryType.EXPENSE.ordinal)
            ?: CategoryType.EXPENSE.ordinal
        CategoryType.entries[typeOrdinal]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load categories with the specified type filter
        viewModel.loadCategories(categoryType)
        // Load most used categories
        viewModel.loadMostUsedCategories(categoryType)
    }

    override fun initView() {
        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewMostUsing.adapter = mostUsingAdapter

        // Setup search
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                viewModel.updateSearchQuery(query, categoryType)
            }
        })
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {
                    // show loading if needed
                }

                is UIState.Success -> {
                    adapter.submitCategories(state.data)
                }

                is UIState.Error -> {
                    // handle error if needed
                }

                else -> {}
            }
        }

        collectFlow(viewModel.mostUsedCategories) { state ->
            when (state) {
                is UIState.Loading -> {
                    // show loading if needed
                }

                is UIState.Success -> {
                    mostUsingAdapter.submitList(state.data)
                    // Show/hide most using section based on data
                    if (state.data.isEmpty()) {
                        binding.textViewMostUsing.visibility = android.view.View.GONE
                        binding.recyclerViewMostUsing.visibility = android.view.View.GONE
                    } else {
                        binding.textViewMostUsing.visibility = android.view.View.VISIBLE
                        binding.recyclerViewMostUsing.visibility = android.view.View.VISIBLE
                    }
                }

                is UIState.Error -> {
                    // Hide most using section on error
                    binding.textViewMostUsing.visibility = android.view.View.GONE
                    binding.recyclerViewMostUsing.visibility = android.view.View.GONE
                }

                else -> {}
            }
        }
    }

    companion object {
        private const val ARG_CATEGORY_TYPE = "category_type"

        /**
         * Factory method to create a new instance of CategoryTabFragment
         * with the specified CategoryType filter
         */
        fun newInstance(categoryType: CategoryType): CategoryTabFragment {
            return CategoryTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CATEGORY_TYPE, categoryType.ordinal)
                }
            }
        }
    }
}

