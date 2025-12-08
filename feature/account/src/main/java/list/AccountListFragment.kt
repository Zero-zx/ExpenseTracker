package list

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.login.databinding.FragmentAccountListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountListFragment : BaseFragment<FragmentAccountListBinding>(
    FragmentAccountListBinding::inflate
) {
    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Account") { AccountTabFragment() },
            TabConfig("Savings") { AccountTabFragment() },
            TabConfig("Accumulate") { AccountTabFragment() }
        )

        binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
    }
}

