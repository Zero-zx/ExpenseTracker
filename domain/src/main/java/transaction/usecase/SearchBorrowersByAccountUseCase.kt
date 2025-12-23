package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Borrower
import transaction.repository.BorrowerRepository
import javax.inject.Inject

class SearchBorrowersByAccountUseCase @Inject constructor(
    private val repository: BorrowerRepository
) {
    operator fun invoke(accountId: Long, searchQuery: String): Flow<List<Borrower>> {
        return repository.searchBorrowersByAccount(accountId, searchQuery)
    }
}

