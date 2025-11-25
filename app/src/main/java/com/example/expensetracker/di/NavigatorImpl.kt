package com.example.expensetracker.di

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

    override fun setNavController(navController: NavController) {
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
}