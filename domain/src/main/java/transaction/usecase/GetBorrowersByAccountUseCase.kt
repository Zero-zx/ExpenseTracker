package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Borrower
import transaction.repository.BorrowerRepository
import javax.inject.Inject

class GetBorrowersByAccountUseCase @Inject constructor(
    private val repository: BorrowerRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Borrower>> {
        return repository.getAllBorrowersByAccount(accountId)
    }
}

