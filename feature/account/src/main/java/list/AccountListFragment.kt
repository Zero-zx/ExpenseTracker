package list

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import base.BaseFragment
import com.example.login.databinding.FragmentAccountListBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountListFragment : BaseFragment<FragmentAccountListBinding>(
    FragmentAccountListBinding::inflate
) {
    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = TabPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val tabTitles = arrayOf("Account", "Savings", "Accumulate")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private class TabPagerAdapter(fragment: AccountListFragment) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AccountTabFragment()
                1 -> AccountTabFragment()
                2 -> AccountTabFragment()
                else -> AccountTabFragment()
            }
        }
    }
}

