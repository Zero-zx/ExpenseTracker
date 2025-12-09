package base

import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Extension function to easily set up TabLayout with ViewPager2
 */
fun ViewPager2.setupWithTabs(
    tabLayout: TabLayout,
    fragment: Fragment,
    tabs: List<TabConfig>
): GenericTabPagerAdapter {
    val adapter = GenericTabPagerAdapter(fragment, tabs)
    this.adapter = adapter

    TabLayoutMediator(tabLayout, this) { tab, position ->
        tab.text = adapter.getTabTitle(position)
    }.attach()

    return adapter
}

