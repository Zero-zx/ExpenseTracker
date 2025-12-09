package base

import androidx.fragment.app.Fragment

/**
 * Configuration for a single tab in a ViewPager
 * @param title The title to display in the tab
 * @param fragmentProvider A lambda that creates the fragment for this tab
 */
data class TabConfig(
    val title: String,
    val fragmentProvider: () -> Fragment
)

