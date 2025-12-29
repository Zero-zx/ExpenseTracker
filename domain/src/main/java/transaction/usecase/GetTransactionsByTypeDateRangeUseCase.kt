package transaction.usecase

import category.model.CategoryType
import kotlinx.coroutines.flow.Flow
import transaction.model.Transaction
import transaction.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionsByTypeDateRangeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(
        startDate: Long,
        endDate: Long,
        types: List<CategoryType>
    ): Flow<List<Transaction>> {
        return transactionRepository.getTransactionsByTypeDateRange(
            startDate,
            endDate,
            types
        )
    }
}

