package base

import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Extension function to easily set up TabLayout with ViewPager2
 * Returns Pair of adapter and mediator for proper lifecycle management
 * 
 * Usage:
 * ```
 * val (adapter, mediator) = binding.viewPager.setupWithTabs(
 *     tabLayout = binding.tabLayout,
 *     fragment = this,
 *     tabs = tabs
 * )
 * tabMediator = mediator
 * 
 * // In onDestroyView():
 * tabMediator?.detach()
 * tabMediator = null
 * ```
 */
fun ViewPager2.setupWithTabs(
    tabLayout: TabLayout,
    fragment: Fragment,
    tabs: List<TabConfig>
): Pair<GenericTabPagerAdapter, TabLayoutMediator> {
    val adapter = GenericTabPagerAdapter(fragment, tabs)
    this.adapter = adapter

    val mediator = TabLayoutMediator(tabLayout, this) { tab, position ->
        tab.text = adapter.getTabTitle(position)
    }
    mediator.attach()

    return adapter to mediator
}

