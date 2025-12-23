package transaction.usecase

import kotlinx.coroutines.flow.Flow
import transaction.model.Lender
import transaction.repository.LenderRepository
import javax.inject.Inject

class GetLendersByAccountUseCase @Inject constructor(
    private val repository: LenderRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Lender>> {
        return repository.getAllLendersByAccount(accountId)
    }
}

