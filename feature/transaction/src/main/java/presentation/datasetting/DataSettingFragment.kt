package presentation.datasetting

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import base.BaseFragment
import base.TabConfig
import base.UIState
import base.setupWithTabs
import com.example.transaction.databinding.FragmentDataSettingBinding
import com.google.android.material.tabs.TabLayoutMediator
import constants.FragmentResultKeys
import dagger.hilt.android.AndroidEntryPoint
import navigation.Navigator
import ui.setSelectionResult
import javax.inject.Inject

@AndroidEntryPoint
class DataSettingFragment : BaseFragment<FragmentDataSettingBinding>(
    FragmentDataSettingBinding::inflate
) {
    val viewModel: DataSettingViewModel by viewModels()

    @Inject
    lateinit var navigator: Navigator
    
    private var tabMediator: TabLayoutMediator? = null

    override fun initView() {
        setupViewPager()
    }

    override fun initListener() {
        binding.imageViewBack.setOnClickListener {
            navigator.navigateUp()
        }
    }

    override fun observeData() {
        // Each tab fragment will observe the ViewModel separately
    }

    private fun setupViewPager() {
        val tabs = listOf(
            TabConfig("Date") { DateTabFragment() },
            TabConfig("Week") { WeekTabFragment() },
            TabConfig("Month") { MonthTabFragment() },
            TabConfig("Q") { QuarterTabFragment() },
            TabConfig("Custom") { CustomTabFragment() }
        )

        val (_, mediator) = binding.viewPager.setupWithTabs(
            tabLayout = binding.tabLayout,
            fragment = this,
            tabs = tabs
        )
        tabMediator = mediator

        // Set default tab to Q (index 3)
        binding.viewPager.setCurrentItem(3, false)

        // Update ViewModel when tab changes
        binding.viewPager.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val tab = when (position) {
                    0 -> DataSettingTab.DATE
                    1 -> DataSettingTab.WEEK
                    2 -> DataSettingTab.MONTH
                    3 -> DataSettingTab.QUARTER
                    4 -> DataSettingTab.CUSTOM
                    else -> DataSettingTab.QUARTER
                }
                viewModel.selectTab(tab)
            }
        })
    }

    fun applySelection() {
        val uiState = viewModel.uiState.value
        val state = (uiState as? UIState.Success)?.data ?: return
        val option = state.selectedOption ?: return

        val (startDate, endDate) = when (option) {
            is DataSettingOption.CustomOption -> {
                if (option.type == CustomOptionType.CUSTOM) {
                    val from = state.customFromDate
                    val to = state.customToDate
                    // Validate that both dates are set
                    if (from == null || to == null) {
                        return // Don't apply if dates are not set
                    }
                    // Ensure from <= to
                    if (from > to) {
                        // Swap if from > to
                        Pair(to, from)
                    } else {
                        Pair(from, to)
                    }
                } else {
                    viewModel.getDateRangeForOption(option)
                }
            }

            else -> viewModel.getDateRangeForOption(option)
        }

        val periodLabel = viewModel.getSelectedPeriodLabel()

        // Pass result back to TransactionListFragment
        setSelectionResult(
            FragmentResultKeys.REQUEST_DATA_SETTING,
            bundleOf(
                FragmentResultKeys.RESULT_START_DATE to startDate,
                FragmentResultKeys.RESULT_END_DATE to endDate,
                FragmentResultKeys.RESULT_PERIOD_LABEL to periodLabel
            )
        )

        navigator.navigateUp()
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }
}

