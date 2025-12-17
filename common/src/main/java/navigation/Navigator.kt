package navigation

interface Navigator {
    fun navigateUp(): Boolean
    fun popBackStack(): Boolean
    fun navigateToTransactionRoute()
    fun navigateToTransaction()
    fun navigateToMoreCategory(categoryType: String = "EXPENSE")
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
    fun navigateToReportDetailContainer(reportType: String = "FINANCIAL_STATEMENT")
    fun navigateToEditTransaction(transactionId: Long)
    fun navigateToDataSetting()
}