package presentation.detail

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.statistics.databinding.FragmentIncomeExpenseDetailBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.model.TabType

@AndroidEntryPoint
class IncomeExpenseDetailFragment : BaseFragment<FragmentIncomeExpenseDetailBinding>(
    FragmentIncomeExpenseDetailBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null

    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Current") { NowTabFragment() },
            TabConfig("Monthly") { ChartTabFragment.newInstance(TabType.MONTHLY) },
            TabConfig("Quarter") { ChartTabFragment.newInstance(TabType.QUARTER) },
            TabConfig("Year") { ChartTabFragment.newInstance(TabType.YEAR) },
            TabConfig("Custom") { ChartTabFragment.newInstance(TabType.CUSTOM) }
        )

        val (_, mediator) = binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
        tabMediator = mediator
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }
}

