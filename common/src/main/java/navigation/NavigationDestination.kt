package navigation

sealed class NavigationDestination(val route: String) {

    // Dashboard destinations
    object Dashboard : NavigationDestination("dashboard")

    // Transaction destinations
    object TransactionList : NavigationDestination("transaction_list")
    object AddTransaction : NavigationDestination("add_transaction")
    data class TransactionDetail(val transactionId: Long) :
        NavigationDestination("transaction_detail/{transactionId}") {
        fun createRoute() = "transaction_detail/$transactionId"
    }
    data class EditTransaction(val transactionId: Long) :
        NavigationDestination("edit_transaction/{transactionId}") {
        fun createRoute() = "edit_transaction/$transactionId"
    }

    // Budget destinations
    object BudgetList : NavigationDestination("budget_list")
    object AddBudget : NavigationDestination("add_budget")

    // Wallet destinations
    object WalletList : NavigationDestination("wallet_list")
    object AddWallet : NavigationDestination("add_wallet")

    // Reports destinations
    object Reports : NavigationDestination("reports")

    // Settings destinations
    object Settings : NavigationDestination("settings")
    object CategoryManagement : NavigationDestination("category_management")
}