package com.example.other.other

import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.other.databinding.FragmentOtherBinding
import com.example.other.other.adapter.SettingAdapter
import com.example.other.other.model.FeatureItem
import com.example.other.other.model.SettingItem
import com.example.other.other.model.UtilityItem
import dagger.hilt.android.AndroidEntryPoint
import ui.showNotImplementToast

@AndroidEntryPoint
class OtherFragment : BaseFragment<FragmentOtherBinding>(
    FragmentOtherBinding::inflate
) {

    private val viewModel: OtherViewModel by viewModels()

    private lateinit var settingAdapter: SettingAdapter

    override fun initView() {
        setupFeatureButtons()
        setupUtilityButtons()
        setupSettingsRecyclerView()
    }

    override fun initListener() {
        binding.apply {
            iconNotification.setOnClickListener {
                showNotImplementToast()
            }

            buttonPremium.setOnClickListener {
                showNotImplementToast()
            }

            buttonCoins.setOnClickListener {
                showNotImplementToast()
            }

            buttonReferral.setOnClickListener {
                showNotImplementToast()
            }
        }
    }

    override fun observeData() {
        collectState(viewModel.userProfile) { profile ->
            profile?.let { updateUserProfile(it) }
        }

        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UIState.Loading -> {
                    // Show loading if needed
                }

                is UIState.Success -> {
                    // Handle success if needed
                }

                is UIState.Error -> {
                    // Handle error if needed
                }

                else -> {}
            }
        }
    }

    private fun setupFeatureButtons() {
        val features = FeatureItem.getDefaultFeatures()

        binding.apply {
            buttonFeatureTheme.setOnClickListener {
                showNotImplementToast()
            }

            buttonFeatureBudget.setOnClickListener {
                showNotImplementToast()
            }

            buttonFeatureCategories.setOnClickListener {
                showNotImplementToast()
            }

            buttonFeatureTemplate.setOnClickListener {
                showNotImplementToast()
            }

            buttonFeatureTravel.setOnClickListener {
                showNotImplementToast()
            }

            buttonFeatureIncomePlan.setOnClickListener {
                showNotImplementToast()
            }

            buttonFeatureShoppingList.setOnClickListener {
                showNotImplementToast()
            }

            buttonFeatureWebsite.setOnClickListener {
                showNotImplementToast()
            }
        }
    }

    private fun setupUtilityButtons() {
        val utilities = UtilityItem.getDefaultUtilities()

        binding.apply {
            buttonUtilityLoan.setOnClickListener {
                showNotImplementToast()
            }

            buttonUtilityExchangeRates.setOnClickListener {
                showNotImplementToast()
            }

            buttonUtilitySavings.setOnClickListener {
                showNotImplementToast()
            }

            buttonUtilityWidget.setOnClickListener {
                showNotImplementToast()
            }

            buttonUtilityExport.setOnClickListener {
                showNotImplementToast()
            }
        }
    }

    private fun setupSettingsRecyclerView() {
        // Setup Settings RecyclerView
        settingAdapter = SettingAdapter { setting ->
            showNotImplementToast()
        }
        binding.recyclerViewSettings.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = settingAdapter
        }
        settingAdapter.submitList(SettingItem.getDefaultSettings())
    }

    private fun updateUserProfile(profile: UserProfile) {
        binding.apply {
            textViewUsername.text = profile.username
            textViewEmail.text = profile.email
            textReferralCode.text = "Referral Code ${profile.referralCode} >"

            // Update avatar initial
            val initial = profile.username.firstOrNull()?.uppercase() ?: "U"
            textViewAvatar.text = initial
        }
    }
}

