package presentation.detail

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.statistics.databinding.FragmentIncomeAnalysisBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.model.TabType

@AndroidEntryPoint
class IncomeAnalysisFragment : BaseFragment<FragmentIncomeAnalysisBinding>(
    FragmentIncomeAnalysisBinding::inflate
) {

    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Date") { IncomeAnalysisTabFragment.newInstance(TabType.NOW) },
            TabConfig("Month") { IncomeAnalysisTabFragment.newInstance(TabType.MONTHLY) },
            TabConfig("Year") { IncomeAnalysisTabFragment.newInstance(TabType.YEAR) }
        )

        binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
    }
}


