package presentation.list

import transaction.model.Transaction

sealed class TransactionListItem {
    data class DateHeader(
        val date: String,
        val dayName: String,
        val totalAmount: Double,
        val transactions: List<Transaction>
    ) : TransactionListItem()

    data class TransactionItem(
        val transaction: Transaction
    ) : TransactionListItem()
}

