package com.example.other.other.model

import androidx.annotation.DrawableRes
import com.example.common.R

data class FeatureItem(
    val id: String,
    val title: String,
    @DrawableRes val iconRes: Int,
    val isNew: Boolean = false
) {
    companion object {
        fun getDefaultFeatures(): List<FeatureItem> = listOf(
            // Row 1
            FeatureItem("theme", "Theme", R.drawable.ic_add_category_light, isNew = true),
            FeatureItem("budget", "Budget", R.drawable.icon_budget),
            FeatureItem("categories", "Categories", R.drawable.chi_1_an_uong),
            FeatureItem("recurring", "Recurring transaction", R.drawable.v2_ic_add_while),
            // Row 2
            FeatureItem("link_bank", "Link bank account", R.drawable.icon_budget, isNew = true),
            FeatureItem("template", "Template record", R.drawable.v2_ic_add_while),
            FeatureItem("travel", "Travel", R.drawable.icon_budget),
            FeatureItem("income_plan", "Income plan/Exp...", R.drawable.icon_budget),
            // Row 3
            FeatureItem("shopping_list", "Shopping list", R.drawable.icon_budget),
            FeatureItem("website", "Use on Website", R.drawable.v2_ic_add_while, isNew = true)
        )
    }
}

