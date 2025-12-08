package presentation.add.ui

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import base.BaseFragment
import base.UIState
import com.example.transaction.databinding.FragmentCategorySelectBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.adapter.ExpandableCategoryAdapter
import presentation.add.viewModel.CategorySelectViewModel
import ui.navigateBack
import ui.setCategoryIdSelectionResult

@AndroidEntryPoint
class CategorySelectFragment : BaseFragment<FragmentCategorySelectBinding>(
    FragmentCategorySelectBinding::inflate
) {
    // Use own ViewModel for category data instead of shared ViewModel
    private val viewModel: CategorySelectViewModel by viewModels()

    private val adapter = ExpandableCategoryAdapter(
        onCategoryClick = { category ->
            // Set the result using the simplified extension function (ID only)
            setCategoryIdSelectionResult(category.id)
            // Navigate back using NavController directly
            navigateBack()
        }
    )

    override fun initView() {
        binding.toolbar.setNavigationOnClickListener {
            navigateBack()
        }

        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CategorySelectFragment.adapter
        }
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
}
