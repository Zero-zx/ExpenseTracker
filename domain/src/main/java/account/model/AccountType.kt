package account.model

import com.example.common.R

enum class AccountType(val rawValue: String, val icon: String? = null) {
    CASH("Cash", "account_wallet"),
    BANK("Bank", "account_back"),
    CREDIT("Credit", "account_master"),
    E_WALLET("Invest", "account_invest"),
    OTHER("Other", "account_other");

    val iconRes: Int
        get() = when (icon) {
            "account_wallet" -> R.drawable.account_wallet
            "account_back" -> R.drawable.account_bank
            "account_master" -> R.drawable.account_master
            "account_invest" -> R.drawable.account_invest
            "account_other" -> R.drawable.account_other
            else -> R.drawable.account_other
        }
}