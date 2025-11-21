package domain.usecase

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import data.model.Transaction
import domain.repository.TransactionRepository

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Transaction>> {
        return transactionRepository.getAllTransactionByAccount(accountId)
    }
}
