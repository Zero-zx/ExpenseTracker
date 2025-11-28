package navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

interface Navigator {
    fun navigateTo(destination: NavigationDestination, navOptions: NavOptions? = null)
    fun navigateUp(): Boolean
    fun popBackStack(): Boolean
    fun navigateToTransaction()
    fun navigateToMoreCategory()
    fun navigateToAccountList()
    fun navigateToAddAccount()
}