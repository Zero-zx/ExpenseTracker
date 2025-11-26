package com.example.expensetracker.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import navigation.NavigationDestination
import navigation.Navigator
import navigation.navigateWithAnim
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorImpl @Inject constructor() : Navigator {

    private var navController: NavController? = null

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun navigateTo(destination: NavigationDestination, navOptions: NavOptions?) {
        val route = when (destination) {
            is NavigationDestination.TransactionDetail -> destination.createRoute()
            is NavigationDestination.EditTransaction -> destination.createRoute()
            else -> destination.route
        }

        navController?.navigateWithAnim(route)
    }

    override fun navigateUp(): Boolean {
        return navController?.navigateUp() ?: false
    }

    override fun popBackStack(): Boolean {
        return navController?.popBackStack() ?: false
    }

    override fun navigateToTransaction() {
        navController?.navigate(com.example.expensetracker.R.id.transactionListFragment)
    }
}