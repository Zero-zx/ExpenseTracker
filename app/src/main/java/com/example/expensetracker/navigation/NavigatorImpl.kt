package com.example.expensetracker.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.expensetracker.R
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

    override fun navigateUp(): Boolean {
        return navController?.navigateUp() ?: false
    }

    override fun popBackStack(): Boolean {
        return navController?.popBackStack() ?: false
    }

    override fun navigateToTransactionRoute() {
        navController?.navigateWithAnim(R.id.transaction_graph)
    }


    override fun navigateToTransaction() {
        navController?.navigateWithAnim(R.id.transactionListFragment)
    }

    override fun navigateToMoreCategory() {
        navController?.navigateWithAnim(R.id.categorySelectFragment)
    }

    override fun navigateToAccountList() {
        navController?.navigateWithAnim(R.id.accountListFragment)
    }

    override fun navigateToAddAccount() {
        navController?.navigateWithAnim(R.id.addAccountFragment)
    }

    override fun navigateToSelectAccount(selectedAccountId: Long) {
        navController?.navigateWithAnim(
            R.id.accountSelectFragment,
            bundleOf("selected_account_id" to selectedAccountId)
        )
    }

    override fun navigateToSelectEvent(selectedEventId: Long) {
        navController?.navigateWithAnim(
            R.id.eventSelectFragment,
            bundleOf("selected_event_id" to selectedEventId)
        )
    }

    override fun navigateToEventList() {
        navController?.navigateWithAnim(R.id.eventListFragment)
    }

    override fun navigateToAddEvent() {
        navController?.navigateWithAnim(R.id.addEventFragment)
    }

    override fun navigateToAddParticipants() {
        navController?.navigateWithAnim(R.id.addParticipantsFragment)
    }

    override fun navigateToIncomeExpenseDetail() {
        navController?.navigateWithAnim(R.id.incomeExpenseDetailFragment)
    }
}