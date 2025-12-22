package com.example.other.other

import androidx.fragment.app.viewModels
import base.BaseFragment
import base.UIState
import com.example.other.databinding.FragmentOtherBinding
import com.example.other.other.adapter.SettingAdapter
import com.example.other.other.model.FeatureItem
import com.example.other.other.model.SettingItem
import com.example.other.other.model.UtilityItem
import com.example.other.other.view.GridIconButton
import dagger.hilt.android.AndroidEntryPoint
import ui.gone
import ui.visible

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
            // Row 1
            buttonFeatureTheme.setIcon(features[0].iconRes)
            buttonFeatureTheme.setTitle(features[0].title)
            buttonFeatureTheme.showNewBadge(features[0].isNew)
            buttonFeatureTheme.setOnClickListener {
                viewModel.navigateToFeature(features[0])
            }

            buttonFeatureBudget.setIcon(features[1].iconRes)
            buttonFeatureBudget.setTitle(features[1].title)
            buttonFeatureBudget.showNewBadge(features[1].isNew)
            buttonFeatureBudget.setOnClickListener {
                viewModel.navigateToFeature(features[1])
            }

            buttonFeatureCategories.setIcon(features[2].iconRes)
            buttonFeatureCategories.setTitle(features[2].title)
            buttonFeatureCategories.showNewBadge(features[2].isNew)
            buttonFeatureCategories.setOnClickListener {
                viewModel.navigateToFeature(features[2])
            }

            buttonFeatureRecurring.setIcon(features[3].iconRes)
            buttonFeatureRecurring.setTitle(features[3].title)
            buttonFeatureRecurring.showNewBadge(features[3].isNew)
            buttonFeatureRecurring.setOnClickListener {
                viewModel.navigateToFeature(features[3])
            }

            // Row 2
            buttonFeatureLinkBank.setIcon(features[4].iconRes)
            buttonFeatureLinkBank.setTitle(features[4].title)
            buttonFeatureLinkBank.showNewBadge(features[4].isNew)
            buttonFeatureLinkBank.setOnClickListener {
                viewModel.navigateToFeature(features[4])
            }

            buttonFeatureTemplate.setIcon(features[5].iconRes)
            buttonFeatureTemplate.setTitle(features[5].title)
            buttonFeatureTemplate.showNewBadge(features[5].isNew)
            buttonFeatureTemplate.setOnClickListener {
                viewModel.navigateToFeature(features[5])
            }

            buttonFeatureTravel.setIcon(features[6].iconRes)
            buttonFeatureTravel.setTitle(features[6].title)
            buttonFeatureTravel.showNewBadge(features[6].isNew)
            buttonFeatureTravel.setOnClickListener {
                viewModel.navigateToFeature(features[6])
            }

            buttonFeatureIncomePlan.setIcon(features[7].iconRes)
            buttonFeatureIncomePlan.setTitle(features[7].title)
            buttonFeatureIncomePlan.showNewBadge(features[7].isNew)
            buttonFeatureIncomePlan.setOnClickListener {
                viewModel.navigateToFeature(features[7])
            }

            // Row 3
            buttonFeatureShoppingList.setIcon(features[8].iconRes)
            buttonFeatureShoppingList.setTitle(features[8].title)
            buttonFeatureShoppingList.showNewBadge(features[8].isNew)
            buttonFeatureShoppingList.setOnClickListener {
                viewModel.navigateToFeature(features[8])
            }

            buttonFeatureWebsite.setIcon(features[9].iconRes)
            buttonFeatureWebsite.setTitle(features[9].title)
            buttonFeatureWebsite.showNewBadge(features[9].isNew)
            buttonFeatureWebsite.setOnClickListener {
                viewModel.navigateToFeature(features[9])
            }
        }
    }

    private fun setupUtilityButtons() {
        val utilities = UtilityItem.getDefaultUtilities()
        
        binding.apply {
            // Row 1
            buttonUtilityLoan.setIcon(utilities[0].iconRes)
            buttonUtilityLoan.setTitle(utilities[0].title)
            buttonUtilityLoan.setOnClickListener {
                viewModel.navigateToUtility(utilities[0])
            }

            buttonUtilityIncomeTax.setIcon(utilities[1].iconRes)
            buttonUtilityIncomeTax.setTitle(utilities[1].title)
            buttonUtilityIncomeTax.setOnClickListener {
                viewModel.navigateToUtility(utilities[1])
            }

            buttonUtilityExchangeRates.setIcon(utilities[2].iconRes)
            buttonUtilityExchangeRates.setTitle(utilities[2].title)
            buttonUtilityExchangeRates.setOnClickListener {
                viewModel.navigateToUtility(utilities[2])
            }

            buttonUtilitySavings.setIcon(utilities[3].iconRes)
            buttonUtilitySavings.setTitle(utilities[3].title)
            buttonUtilitySavings.setOnClickListener {
                viewModel.navigateToUtility(utilities[3])
            }

            // Row 2
            buttonUtilitySplitMoney.setIcon(utilities[4].iconRes)
            buttonUtilitySplitMoney.setTitle(utilities[4].title)
            buttonUtilitySplitMoney.setOnClickListener {
                viewModel.navigateToUtility(utilities[4])
            }

            buttonUtilityWidget.setIcon(utilities[5].iconRes)
            buttonUtilityWidget.setTitle(utilities[5].title)
            buttonUtilityWidget.setOnClickListener {
                viewModel.navigateToUtility(utilities[5])
            }

            buttonUtilityMisaAsp.setIcon(utilities[6].iconRes)
            buttonUtilityMisaAsp.setTitle(utilities[6].title)
            buttonUtilityMisaAsp.setOnClickListener {
                viewModel.navigateToUtility(utilities[6])
            }

            buttonUtilityPremium.setIcon(utilities[7].iconRes)
            buttonUtilityPremium.setTitle(utilities[7].title)
            buttonUtilityPremium.setOnClickListener {
                viewModel.navigateToUtility(utilities[7])
            }

            // Row 3
            buttonUtilityExport.setIcon(utilities[8].iconRes)
            buttonUtilityExport.setTitle(utilities[8].title)
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

