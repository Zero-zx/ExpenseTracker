//package navigation
//
//import androidx.navigation.fragment.fragment
//import androidx.navigation.NavGraphBuilder
//import presentation.add.TransactionAddFragment
//import presentation.list.TransactionListFragment
//
///**
// * Extension function để add transaction navigation graph
// * Được gọi từ AppNavGraph trong app module
// */
//fun NavGraphBuilder.transactionGraph() {
//
//    // Transaction List Screen
//    fragment<TransactionListFragment>(
//        route = NavigationDestination.TransactionList.route
//    )
//
//    // Add Transaction Screen
//    fragment<TransactionAddFragment>(
//        route = NavigationDestination.AddTransaction.route
//    )
//}
