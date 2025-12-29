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
            SettingItem("data", "Data", null, R.drawable.custom_ic_setting_data_v2),
            SettingItem("share", "Share with friends", null, R.drawable.custom_ic_setting_share_v2),
            SettingItem(
                "rate",
                "Rate app",
                "Please rate us 5 stars on Google Play!",
                com.example.other.R.drawable.custom_icon_setting_rate_app_v2
            ),
            SettingItem("feedback", "Feedback", null, R.drawable.custom_ic_setting_feedback_v2),
            SettingItem(
                "help",
                "Help and Information",
                null,
                com.example.other.R.drawable.v2_ic_setting_help_white
            )
        )
    }
}

