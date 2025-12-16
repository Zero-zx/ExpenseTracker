package presentation.add.ui

import androidx.core.os.bundleOf
import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.transaction.databinding.FragmentEventSelectBinding
import com.google.android.material.tabs.TabLayoutMediator
import constants.FragmentResultKeys.REQUEST_SELECT_PAYEE_IDS
import constants.FragmentResultKeys.RESULT_PAYEE_IDS
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.model.PayeeTabType
import ui.navigateBack
import ui.setSelectionResult

@AndroidEntryPoint
class PayeeSelectFragment : BaseFragment<FragmentEventSelectBinding>(
    FragmentEventSelectBinding::inflate
) {
    private val selectedPayeeIds: MutableSet<Long> = mutableSetOf()
    private var tabMediator: TabLayoutMediator? = null

    fun getSelectedPayeeIds(): Set<Long> = selectedPayeeIds.toSet()

    override fun initView() {
        // Get initially selected payee IDs from arguments
        arguments?.getLongArray("selected_payee_ids")?.let {
            selectedPayeeIds.addAll(it.toList())
        }

        setupViewPager()
    }

    override fun initListener() {
        binding.apply {
            buttonBack.setOnClickListener {
                navigateBack()
            }
        }
    }

    fun onPayeeToggled(payeeId: Long) {
        if (selectedPayeeIds.contains(payeeId)) {
            selectedPayeeIds.remove(payeeId)
        } else {
            selectedPayeeIds.add(payeeId)
        }
        updateTabSelections()
    }

    fun onConfirmSelection() {
        setSelectionResult(
            REQUEST_SELECT_PAYEE_IDS,
            bundleOf(RESULT_PAYEE_IDS to selectedPayeeIds.toLongArray())
        )
        navigateBack()
    }

    private fun updateTabSelections() {
        // Update selection in child fragments
        childFragmentManager.fragments.forEach { fragment ->
            if (fragment is PayeeTabFragment) {
                fragment.refreshSelection()
            }
        }
    }

    fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Recent") { PayeeTabFragment.newInstance(PayeeTabType.RECENT) },
            TabConfig("Contact") { PayeeTabFragment.newInstance(PayeeTabType.CONTACTS) }
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
