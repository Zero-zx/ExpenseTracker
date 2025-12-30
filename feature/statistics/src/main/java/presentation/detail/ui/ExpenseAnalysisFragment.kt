package presentation.detail.ui

import androidx.fragment.app.Fragment
import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.statistics.databinding.FragmentExpenseAnalysisBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.model.TabType

@AndroidEntryPoint
class ExpenseAnalysisFragment : BaseFragment<FragmentExpenseAnalysisBinding>(
    FragmentExpenseAnalysisBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null

    override fun initView() {
        setupViewPager()
        listenForResults()
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Date") { ExpenseAnalysisTabFragment.newInstance(TabType.NOW) },
            TabConfig("Month") { ExpenseAnalysisTabFragment.newInstance(TabType.MONTHLY) },
            TabConfig("Year") { ExpenseAnalysisTabFragment.newInstance(TabType.YEAR) }
        )

        val (_, mediator) = binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
        tabMediator = mediator
    }

    fun listenForResults() {

    }

    private fun getCurrentTabFragment(): Fragment? {
        val position = binding.viewPager.currentItem
        return childFragmentManager.findFragmentByTag("f$position")
    }


    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }
}


