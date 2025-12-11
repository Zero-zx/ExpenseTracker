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
        return navController?.navigateUp() ?: false
    }

    override fun navigateToTransactionRoute() {
        navController?.navigateWithAnim(R.id.transaction_nav_graph)
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

    override fun navigateToSelectPayee(selectedPayeeIds: LongArray) {
        navController?.navigateWithAnim(
            R.id.payeeSelectFragment,
            bundleOf("selected_payee_ids" to selectedPayeeIds)
        )
    }

    override fun navigateToSelectLocation(selectedLocationId: Long) {
        navController?.navigateWithAnim(
            R.id.locationSelectFragment,
            bundleOf("selected_location_id" to selectedLocationId)
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

    override fun navigateToEditTransaction(transactionId: Long) {
        navController?.navigateWithAnim(
            R.id.transactionAddFragment,
            bundleOf("transaction_id" to transactionId)
        )
    }
}