package presentation.detail

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.statistics.databinding.FragmentExpenseAnalysisBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.model.TabType

@AndroidEntryPoint
class ExpenseAnalysisFragment : BaseFragment<FragmentExpenseAnalysisBinding>(
    FragmentExpenseAnalysisBinding::inflate
) {

    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Date") { ExpenseAnalysisTabFragment.newInstance(TabType.NOW) },
            TabConfig("Month") { ExpenseAnalysisTabFragment.newInstance(TabType.MONTHLY) },
            TabConfig("Year") { ExpenseAnalysisTabFragment.newInstance(TabType.YEAR) }
        )

        binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
    }
}


