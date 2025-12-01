package account.model

import com.example.common.R

enum class AccountType(val rawValue: String, val icon: String? = null) {
    CASH("Cash", "icon_cash"),
    BANK("Bank", "icon_bank"),
    CREDIT("Credit", "icon_credit"),
    E_WALLET("E-wallet", "icon_e_wallet"),
    OTHER("Other", "icon_other");

    val iconRes: Int
        get() = when (icon) {
            "icon_cash" -> R.drawable.icon_cash
            "icon_bank" -> R.drawable.icon_bank
            "icon_credit" -> R.drawable.icon_credit
            "icon_e_wallet" -> R.drawable.icon_e_wallet
            "icon_other" -> R.drawable.icon_other
            else -> R.drawable.icon_other
        }
}