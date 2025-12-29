package presentation.add.ui

import base.BaseFragment
import base.TabConfig
import base.setupWithTabs
import com.example.transaction.databinding.FragmentLocationSelectBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import presentation.add.model.LocationTabType
import ui.navigateBack
import ui.setLocationNameSelectionResult

@AndroidEntryPoint
class LocationSelectFragment : BaseFragment<FragmentLocationSelectBinding>(
    FragmentLocationSelectBinding::inflate
) {
    private var tabMediator: TabLayoutMediator? = null
    private val selectedLocationName: String by lazy {
        arguments?.getString("selected_location_name", "") ?: ""
    }

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

    fun onLocationSelected(locationName: String) {
        setLocationNameSelectionResult(locationName)
        navigateBack()
    }

    fun setupViewPager() {
        val tabs = listOf(
            TabConfig("In Progress") { 
                LocationTabFragment.newInstance(LocationTabType.IN_PROGRESS).apply {
                    arguments?.putString(LocationTabFragment.ARG_SELECTED_LOCATION_NAME, selectedLocationName)
                }
            },
            TabConfig("In Complete") { 
                LocationTabFragment.newInstance(LocationTabType.IN_COMPLETE).apply {
                    arguments?.putString(LocationTabFragment.ARG_SELECTED_LOCATION_NAME, selectedLocationName)
                }
            }
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

