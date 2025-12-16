package presentation.detail

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.statistics.databinding.FragmentIncomeAnalysisBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.model.TabType

@AndroidEntryPoint
class IncomeAnalysisFragment : BaseFragment<FragmentIncomeAnalysisBinding>(
    FragmentIncomeAnalysisBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null

    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Date") { IncomeAnalysisTabFragment.newInstance(TabType.NOW) },
            TabConfig("Month") { IncomeAnalysisTabFragment.newInstance(TabType.MONTHLY) },
            TabConfig("Year") { IncomeAnalysisTabFragment.newInstance(TabType.YEAR) }
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


