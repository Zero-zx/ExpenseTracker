package com.example.other.other.model

import androidx.annotation.DrawableRes
import com.example.common.R

data class SettingItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    @DrawableRes val iconRes: Int
) {
    companion object {
        fun getDefaultSettings(): List<SettingItem> = listOf(
            SettingItem("general", "General settings", null, R.drawable.custom_ic_setting_v2),
            SettingItem("data", "Data", null, R.drawable.icon_budget),
            SettingItem("share", "Share with friends", null, R.drawable.v2_ic_add_while),
            SettingItem("rate", "Rate app", "Please rate us 5 stars on Google Play!", R.drawable.v2_ic_add_while),
            SettingItem("feedback", "Feedback", null, R.drawable.v2_ic_add_while),
            SettingItem("help", "Help and Information", null, R.drawable.v2_ic_add_while)
        )
    }
}

