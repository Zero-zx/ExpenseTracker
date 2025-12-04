package usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsByDateRangeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(accountId: Long, startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionRepository.getTransactionsByDateRange(accountId, startDate, endDate)
    }
}