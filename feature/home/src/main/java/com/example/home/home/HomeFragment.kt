package com.example.home.home

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import base.BaseFragment
import com.example.home.databinding.FragmentHomeBinding
import com.example.home.databinding.MenuItemTimePeriodBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()
    private var selectedTimePeriod = "This Month"

    override fun initListener() {
        binding.apply {
            buttonArrow.setOnClickListener {
                // Navigate to accounts or balance detail
            }

            iconRefresh.setOnClickListener {
                // Refresh data
            }

            iconNotification.setOnClickListener {
                // Open notifications
            }

            iconEye.setOnClickListener {
                // Toggle balance visibility
            }

            buttonSettings.setOnClickListener {
                // Open expense vs income settings
            }

            buttonTimePeriod.setOnClickListener {
                // Show time period selection popup
                showTimePeriodPopup()
            }

            buttonRecordHistory.setOnClickListener {
                // Navigate to record history
                viewModel.navigateToTransaction()
            }
        }
    }

    private fun showTimePeriodPopup() {
        val timePeriods = listOf("Today", "This Week", "This Month", "Quarter Present", "This Year")

        val popupView = LayoutInflater.from(requireContext()).inflate(
            android.R.layout.simple_list_item_1,
            null
        )

        val popupWindow = PopupWindow(
            popupView,
            binding.buttonTimePeriod.width,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Show popup below the button
        popupWindow.showAsDropDown(binding.buttonTimePeriod, 0, 8)

        // TODO: Implement proper popup with custom layout showing time periods
        // For now, just update the selected period when clicked
    }
}