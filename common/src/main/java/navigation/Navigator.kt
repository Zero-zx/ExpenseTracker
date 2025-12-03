package navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

interface Navigator {
    fun navigateUp(): Boolean
    fun popBackStack(): Boolean
    fun navigateToTransactionRoute()
    fun navigateToTransaction()
    fun navigateToMoreCategory()
    fun navigateToAccountList()
    fun navigateToAddAccount()
    fun navigateToSelectAccount()
    fun navigateToSelectEvent()
    fun navigateToEventList()
    fun navigateToAddEvent()
    fun navigateToAddParticipants()
}