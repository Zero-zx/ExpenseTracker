package list

import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.login.databinding.FragmentAccountListBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import model.AccountTabType
import ui.gone
import ui.visible

@AndroidEntryPoint
class AccountListFragment : BaseFragment<FragmentAccountListBinding>(
    FragmentAccountListBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null
    private val viewModel: AccountListViewModel by viewModels()

    override fun initView() {
        setupViewPager()
    }

    override fun initListener() {
        binding.fabAddAccount.setOnClickListener {
            viewModel.navigateToAddAccount()
        }
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Account") { AccountTabFragment.newInstance(AccountTabType.ACCOUNT) },
            TabConfig("Savings") { AccountTabFragment.newInstance(AccountTabType.SAVINGS) },
            TabConfig("Accumulate") { AccountTabFragment.newInstance(AccountTabType.ACCUMULATE) }
        )

        val (_, mediator) = binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
        tabMediator = mediator

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> {
                        showFabButton()
                    }

                    1 -> {
                        hideFabButton()
                    }
                }
            }
        })
    }

    fun hideFabButton() {
        binding.fabAddAccount.hide()
    }

    fun showFabButton() {
        binding.fabAddAccount.show()
    }

    fun showMenuButton() {
        binding.buttonSort.visible()
    }

    fun hideMenuButton() {
        binding.buttonSort.gone()
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }
}

