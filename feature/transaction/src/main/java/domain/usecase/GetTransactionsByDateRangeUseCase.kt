package domain.usecase

import data.model.Transaction
import domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsByDateRangeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(accountId: Long, startDate: Long, endDate: Long): Flow<List<Transaction>> {
        return transactionRepository.getTransactionsByDateRange(accountId, startDate, endDate)
    }
}
