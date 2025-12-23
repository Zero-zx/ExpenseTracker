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
                // TODO: Navigate to notifications
            }

            buttonPremium.setOnClickListener {
                viewModel.navigateToPremium()
            }

            buttonCoins.setOnClickListener {
                // TODO: Navigate to coins screen
            }

            buttonReferral.setOnClickListener {
                // TODO: Show referral code dialog or navigate
            }

            cardSync.setOnClickListener {
                viewModel.sync()
            }

            buttonSupport.setOnClickListener {
                // TODO: Open support/help
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
                viewModel.navigateToFeature(features[0])
            }

            buttonFeatureBudget.setOnClickListener {
                viewModel.navigateToFeature(features[1])
            }

            buttonFeatureCategories.setOnClickListener {
                viewModel.navigateToFeature(features[2])
            }

            buttonFeatureRecurring.setOnClickListener {
                viewModel.navigateToFeature(features[3])
            }


            buttonFeatureTemplate.setOnClickListener {
                viewModel.navigateToFeature(features[5])
            }

            buttonFeatureTravel.setOnClickListener {
                viewModel.navigateToFeature(features[6])
            }

            buttonFeatureIncomePlan.setOnClickListener {
                viewModel.navigateToFeature(features[7])
            }

            buttonFeatureShoppingList.setOnClickListener {
                viewModel.navigateToFeature(features[8])
            }

            buttonFeatureWebsite.setOnClickListener {
                viewModel.navigateToFeature(features[9])
            }
        }
    }

    private fun setupUtilityButtons() {
        val utilities = UtilityItem.getDefaultUtilities()
        
        binding.apply {
            buttonUtilityLoan.setOnClickListener {
                viewModel.navigateToUtility(utilities[0])
            }

            buttonUtilityExchangeRates.setOnClickListener {
                viewModel.navigateToUtility(utilities[2])
            }

            buttonUtilitySavings.setOnClickListener {
                viewModel.navigateToUtility(utilities[3])
            }

            buttonUtilityWidget.setOnClickListener {
                viewModel.navigateToUtility(utilities[5])
            }

            buttonUtilityExport.setOnClickListener {
                viewModel.navigateToUtility(utilities[8])
            }
        }
    }

    private fun setupSettingsRecyclerView() {
        // Setup Settings RecyclerView
        settingAdapter = SettingAdapter { setting ->
            viewModel.navigateToSetting(setting)
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

