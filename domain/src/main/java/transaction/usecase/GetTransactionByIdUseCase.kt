package transaction.usecase

import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: Long): Transaction? {
        return repository.getTransactionById(transactionId)
    }
}
