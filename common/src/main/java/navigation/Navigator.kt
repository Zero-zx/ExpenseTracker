package navigation

interface Navigator {
    fun navigateUp(): Boolean
    fun popBackStack(): Boolean
    fun navigateToTransactionRoute()
    fun navigateToTransaction()
    fun navigateToMoreCategory()
    fun navigateToAccountList()
    fun navigateToAddAccount()
    fun navigateToSelectAccount(selectedAccountId: Long = -1L)
    fun navigateToSelectEvent(selectedEventId: Long = -1L)
    fun navigateToSelectPayee(selectedPayeeIds: LongArray = longArrayOf())
    fun navigateToSelectLocation(selectedLocationId: Long = -1L)
    fun navigateToEventList()
    fun navigateToAddEvent()
    fun navigateToAddParticipants()
    fun navigateToIncomeExpenseDetail()
    fun navigateToEditTransaction(transactionId: Long)
}