package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Lender
import transaction.repository.LenderRepository
import javax.inject.Inject

class SearchLendersByAccountUseCase @Inject constructor(
    private val repository: LenderRepository
) {
    operator fun invoke(accountId: Long, searchQuery: String): Flow<List<Lender>> {
        return repository.searchLendersByAccount(accountId, searchQuery)
    }
}

