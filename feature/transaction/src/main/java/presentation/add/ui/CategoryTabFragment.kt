package presentation.add.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentCategoryTabBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.ExpandableCategoryAdapter
import presentation.add.viewModel.CategoryTabViewModel
import transaction.model.CategoryType

@AndroidEntryPoint
class CategoryTabFragment : BaseFragment<FragmentCategoryTabBinding>(
    FragmentCategoryTabBinding::inflate
) {
    private val viewModel: CategoryTabViewModel by viewModels()

    private val adapter = ExpandableCategoryAdapter(
        onCategoryClick = { category ->
            // Notify parent fragment about category selection
            (parentFragment as? CategorySelectFragment)?.onCategorySelected(category.id)
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
    }

    override fun initView() {
        binding.recyclerViewCategories.adapter = adapter
    }

    override fun observeData() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {
                    // show loading if needed
                }

                is UIState.Success -> adapter.submitCategories(state.data)
                is UIState.Error -> {
                    // handle error if needed
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

