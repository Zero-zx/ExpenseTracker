package presentation.add.ui

import androidx.core.os.bundleOf
import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.common.R
import com.example.transaction.databinding.FragmentEventSelectBinding
import com.google.android.material.tabs.TabLayoutMediator
import constants.FragmentResultKeys.REQUEST_SELECT_PAYEE_NAMES
import constants.FragmentResultKeys.RESULT_PAYEE_NAMES
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.model.PayeeTabType
import ui.navigateBack
import ui.setSelectionResult

@AndroidEntryPoint
class PayeeSelectFragment : BaseFragment<FragmentEventSelectBinding>(
    FragmentEventSelectBinding::inflate
) {
    private val selectedPayeeNames: MutableSet<String> = mutableSetOf()
    private var tabMediator: TabLayoutMediator? = null

    fun getSelectedPayeeNames(): Set<String> = selectedPayeeNames.toSet()

    override fun initView() {
        // Get initially selected payee IDs from arguments
        arguments?.getStringArray("selected_payee_names")?.let {
            selectedPayeeNames.addAll(it.toList())
        }

        binding.textViewTitle.text = getString(R.string.text_with_whom)

        setupViewPager()
    }

    override fun initListener() {
        binding.apply {
            buttonBack.setOnClickListener {
                // Pass back selected payees when user clicks back (similar to event but for multiple selection)
                onConfirmSelection()
            }
        }
    }

    fun onPayeeToggled(payeeName: String) {
        if (selectedPayeeNames.contains(payeeName)) {
            selectedPayeeNames.remove(payeeName)
        } else {
            selectedPayeeNames.add(payeeName)
        }
        updateTabSelections()
    }

    fun onConfirmSelection() {
        setSelectionResult(
            REQUEST_SELECT_PAYEE_NAMES,
            bundleOf(RESULT_PAYEE_NAMES to selectedPayeeNames.toTypedArray())
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
