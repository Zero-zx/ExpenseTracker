package com.example.other.other.model

import androidx.annotation.DrawableRes
import com.example.common.R

data class UtilityItem(
    val id: String,
    val title: String,
    @DrawableRes val iconRes: Int
) {
    companion object {
        fun getDefaultUtilities(): List<UtilityItem> = listOf(
            // Row 1
            UtilityItem("loan_calculator", "Calculate loan inter...", R.drawable.icon_budget),
            UtilityItem("income_tax", "Personal Income T...", R.drawable.icon_budget),
            UtilityItem("exchange_rates", "Exchange rates utility", R.drawable.icon_budget),
            UtilityItem("savings", "Savings", R.drawable.icon_saving),
            // Row 2
            UtilityItem("split_money", "Split money", R.drawable.icon_budget),
            UtilityItem("widget", "Widget", R.drawable.v2_ic_add_while),
            UtilityItem("misa_asp", "MISA ASP", R.drawable.icon_budget),
            UtilityItem("premium_free", "Premium miễn phí", R.drawable.icon_budget),
            // Row 3
            UtilityItem("export_data", "Export data", R.drawable.icon_budget)
        )
    }
}

