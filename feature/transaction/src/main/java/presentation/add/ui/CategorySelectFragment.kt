package presentation.add.ui

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.transaction.databinding.FragmentCategorySelectBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import category.model.CategoryType
import ui.navigateBack
import ui.setCategoryIdSelectionResult

@AndroidEntryPoint
class CategorySelectFragment : BaseFragment<FragmentCategorySelectBinding>(
    FragmentCategorySelectBinding::inflate
) {
    private var selectedCategoryId: Long? = null
    private var tabMediator: TabLayoutMediator? = null
    private var initialCategoryType: CategoryType = CategoryType.EXPENSE

    fun getSelectedCategoryId(): Long? = selectedCategoryId

    override fun initView() {
        // Get initially selected category ID from arguments
        arguments?.getLong("selected_category_id", -1L)?.takeIf { it != -1L }?.let {
            selectedCategoryId = it
        }

        // Get category type from arguments to set default tab
        arguments?.getString("category_type")?.let { typeString ->
            initialCategoryType = CategoryType.valueOf(typeString)
        }

        setupViewPager()
    }

    override fun initListener() {
        binding.buttonBack.setOnClickListener {
            navigateBack()
        }
    }

    fun onCategorySelected(categoryId: Long) {
        selectedCategoryId = categoryId
        onConfirmSelection()
    }

    private fun onConfirmSelection() {
        selectedCategoryId?.let { categoryId ->
            setCategoryIdSelectionResult(categoryId)
            navigateBack()
        }
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Expense") { CategoryTabFragment.newInstance(CategoryType.EXPENSE) },
            TabConfig("Income") { CategoryTabFragment.newInstance(CategoryType.INCOME) },
            TabConfig("Lent/Borrowed") { CategoryTabFragment.newInstance(CategoryType.LEND) }
        )

        val (_, mediator) = binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
        tabMediator = mediator

        // Set the initial tab based on the category type
        val initialTabPosition = when (initialCategoryType) {
            CategoryType.EXPENSE -> 0
            CategoryType.INCOME -> 1
            CategoryType.LEND -> 2
            CategoryType.BORROWING -> 2
            else -> 0
        }
        binding.viewPager.setCurrentItem(initialTabPosition, false)
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }
}
