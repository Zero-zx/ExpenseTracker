package transaction.usecase

import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for deleting transactions.
 */
class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return try {
            repository.deleteTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransactions(transactions: List<Transaction>): Result<Unit> {
        return try {
            transactions.forEach { transaction ->
                repository.deleteTransaction(transaction)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


