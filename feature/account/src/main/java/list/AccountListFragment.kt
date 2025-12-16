package list

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.login.databinding.FragmentAccountListBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountListFragment : BaseFragment<FragmentAccountListBinding>(
    FragmentAccountListBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null

    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Account") { AccountTabFragment() },
            TabConfig("Savings") { AccountTabFragment() },
            TabConfig("Accumulate") { AccountTabFragment() }
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

