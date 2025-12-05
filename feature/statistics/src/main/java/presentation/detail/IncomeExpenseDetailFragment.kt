package presentation.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import base.BaseFragment
import com.example.statistics.databinding.FragmentIncomeExpenseDetailBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import presentation.detail.model.TabType

@AndroidEntryPoint
class IncomeExpenseDetailFragment : BaseFragment<FragmentIncomeExpenseDetailBinding>(
    FragmentIncomeExpenseDetailBinding::inflate
) {

    override fun initView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = TabPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val tabTitles = arrayOf("Now", "Monthly", "Quarter", "Year", "Custom")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private class TabPagerAdapter(fragment: IncomeExpenseDetailFragment) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> NowTabFragment()
                1 -> ChartTabFragment.newInstance(TabType.MONTHLY)
                2 -> ChartTabFragment.newInstance(TabType.QUARTER)
                3 -> ChartTabFragment.newInstance(TabType.YEAR)
                4 -> ChartTabFragment.newInstance(TabType.CUSTOM)
                else -> NowTabFragment()
            }
        }
    }
}

