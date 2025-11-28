package data.model

enum class AccountType(val rawValue: String) {
    NORMAL("normal"),
    PREMIUM("premium"),
    CASH("cash"),
    BANK("bank"),
    CREDIT("credit"),
    E_WALLET("e-wallet"),
    OTHER("other")
}