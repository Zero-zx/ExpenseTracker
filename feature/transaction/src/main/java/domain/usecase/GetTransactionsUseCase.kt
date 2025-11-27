package domain.usecase

import kotlinx.coroutines.flow.Flow
import data.model.Transaction
import domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Transaction>> {
        return transactionRepository.getAllTransactionByAccount(accountId)
    }
}
