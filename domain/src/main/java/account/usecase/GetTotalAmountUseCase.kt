package account.usecase

import kotlinx.coroutines.flow.Flow
import transaction.repository.TransactionRepository
import javax.inject.Inject

class GetTotalAmountUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<Double> {
        return transactionRepository.getTotalBalance()
    }
}