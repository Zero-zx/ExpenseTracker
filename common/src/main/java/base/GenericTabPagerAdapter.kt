package base

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Generic adapter for TabLayout + ViewPager2 combination
 * Eliminates code duplication across multiple screens using tabs
 *
 * Usage:
 * ```
 * val tabs = listOf(
 *     TabConfig("Tab 1") { Fragment1() },
 *     TabConfig("Tab 2") { Fragment2() }
 * )
 * val adapter = GenericTabPagerAdapter(this, tabs)
 * binding.viewPager.adapter = adapter
 * TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
 *     tab.text = adapter.getTabTitle(position)
 * }.attach()
 * ```
 */
class GenericTabPagerAdapter(
    fragment: Fragment,
    private val tabs: List<TabConfig>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return tabs.getOrNull(position)?.fragmentProvider?.invoke()
            ?: throw IllegalStateException("No tab configuration found for position $position")
    }

    /**
     * Get the title for a tab at the given position
     */
    fun getTabTitle(position: Int): String {
        return tabs.getOrNull(position)?.title
            ?: throw IllegalStateException("No tab configuration found for position $position")
    }
}

