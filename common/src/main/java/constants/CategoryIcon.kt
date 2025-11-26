package constants

import androidx.annotation.DrawableRes
import com.example.common.R

enum class CategoryIcon(
    @DrawableRes val iconRes: Int,
    val iconName: String
) {
    FOOD(R.drawable.icon_food, "food"),
    TRANSPORT(R.drawable.icon_transportation, "transport"),
    TRAVEL(R.drawable.icon_holiday, "travel"),
    SALARY(R.drawable.icon_salary, "salary"),
    HOME(R.drawable.icon_home, "home");

    companion object {
        fun fromName(name: String): CategoryIcon {
            return entries.find { it.iconName == name } ?: SALARY
        }
    }
}