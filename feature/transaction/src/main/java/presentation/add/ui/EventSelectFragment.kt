package presentation.add.ui

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.transaction.databinding.FragmentEventSelectBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.model.EventTabType
import ui.navigateBack
import ui.setEventNameSelectionResult

@AndroidEntryPoint
class EventSelectFragment : BaseFragment<FragmentEventSelectBinding>(
    FragmentEventSelectBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null

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

    fun onEventSelected(eventName: String) {
        setEventNameSelectionResult(eventName)
        navigateBack()
    }

    fun setupViewPager() {
        val tabs = listOf(
            TabConfig("In Progress") { EventTabFragment.newInstance(EventTabType.IN_PROGRESS) },
            TabConfig("In Complete") { EventTabFragment.newInstance(EventTabType.IN_COMPLETE) }
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
