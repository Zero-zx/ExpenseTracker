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
    HOME(R.drawable.icon_home, "home"),
    STUDY(R.drawable.icon_study, "study"),
    BUS(R.drawable.icon_bus, "bus"),
    SPORT(R.drawable.icon_sport, "sport"),
    CLOTHES(R.drawable.icon_clothes, "clothes"),
    ATM(R.drawable.icon_atm, "atm"),
    BABY(R.drawable.icon_baby, "baby"),
    MOVIE(R.drawable.icon_movie, "movie"),
    CASH(R.drawable.icon_cash, "icon_cash"),
    BANK(R.drawable.icon_bank, "icon_bank"),
    CREDIT(R.drawable.icon_credit, "icon_credit"),
    E_WALLET(R.drawable.icon_e_wallet, "icon_e_wallet"),
    OTHER(R.drawable.icon_other, "icon_other");

    companion object {
        fun fromName(name: String): CategoryIcon {
            return entries.find { it.iconName == name } ?: OTHER
        }
    }
}