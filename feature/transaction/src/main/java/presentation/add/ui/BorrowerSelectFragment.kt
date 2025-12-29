package presentation.add.ui

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.common.R
import com.example.transaction.databinding.FragmentEventSelectBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.model.PayeeTabType
import ui.navigateBack
import ui.setBorrowerNameSelectionResult

@AndroidEntryPoint
class BorrowerSelectFragment : BaseFragment<FragmentEventSelectBinding>(
    FragmentEventSelectBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null

    override fun initView() {
        setupViewPager()
        binding.textViewTitle.text = getString(R.string.text_borrower)
    }

    override fun initListener() {
        binding.apply {
            buttonBack.setOnClickListener {
                navigateBack()
            }
        }
    }

    fun onBorrowerSelected(borrowerName: String) {
        setBorrowerNameSelectionResult(borrowerName)
        navigateBack()
    }

    fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Recent") { BorrowerTabFragment.newInstance(PayeeTabType.RECENT) },
            TabConfig("Contact") { BorrowerTabFragment.newInstance(PayeeTabType.CONTACTS) }
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
