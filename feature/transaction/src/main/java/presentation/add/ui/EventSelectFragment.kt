package presentation.add.ui

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.transaction.databinding.FragmentEventSelectBinding
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.model.EventTabType
import ui.navigateBack

@AndroidEntryPoint
class EventSelectFragment : BaseFragment<FragmentEventSelectBinding>(
    FragmentEventSelectBinding::inflate
) {

    override fun initView() {
        setupViewPager()
    }

    override fun initListener() {
        binding.apply {
            buttonBack.setOnClickListener {
                navigateBack()
            }
        }
    }

    fun setupViewPager() {
        val tabs = listOf(
            TabConfig("In Progress") { EventTabFragment.newInstance(EventTabType.IN_PROGRESS) },
            TabConfig("In Complete") { EventTabFragment.newInstance(EventTabType.IN_COMPLETE) }
        )

        binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
    }
}
