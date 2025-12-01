package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Transaction>> {
        return transactionRepository.getAllTransactionByAccount(accountId)
    }
}


