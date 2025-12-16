package presentation.add.ui

import androidx.core.os.bundleOf
import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.transaction.databinding.FragmentEventSelectBinding
import com.google.android.material.tabs.TabLayoutMediator
import constants.FragmentResultKeys.REQUEST_SELECT_EVENT_ID
import constants.FragmentResultKeys.RESULT_EVENT_ID
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.model.EventTabType
import transaction.model.Event
import ui.navigateBack
import ui.setEventIdSelectionResult

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

    fun onEventSelected(eventId: Long) {
        setEventIdSelectionResult(eventId)
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
