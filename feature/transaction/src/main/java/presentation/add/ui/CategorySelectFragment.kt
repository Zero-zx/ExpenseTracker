package presentation.add.ui

import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import presentation.CategoryUiState
import com.example.transaction.R
import com.example.transaction.databinding.FragmentCategorySelectBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.ExpandableCategoryAdapter
import presentation.add.viewModel.AddTransactionViewModel

@AndroidEntryPoint
class CategorySelectFragment : BaseFragment<FragmentCategorySelectBinding>(
    FragmentCategorySelectBinding::inflate
) {
    // shared nav-graph scoped VM for storing selected category and providing categories
    private val sharedVm: AddTransactionViewModel by navGraphViewModels(R.id.transaction_graph) { defaultViewModelProviderFactory }

    private val adapter = ExpandableCategoryAdapter(
        onCategoryClick = { category ->
            sharedVm.selectCategory(category)
            sharedVm.navigateBack()
        }
    )

    override fun initView() {
        binding.toolbar.setNavigationOnClickListener {
            // Use shared navigator via shared VM to navigate back instead of deprecated onBackPressed
            sharedVm.navigateBack()
        }

        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategorySelectFragment.adapter
        }
    }

    override fun observeData() {
        collectState(sharedVm.categoryState) { state: CategoryUiState ->
            when (state) {
                is CategoryUiState.Loading -> {
                    // show loading if needed
                }
                is CategoryUiState.Success -> adapter.submitCategories(state.categories)
                is CategoryUiState.Error -> {
                    // handle error if needed
                }
            }
        }
    }
}
