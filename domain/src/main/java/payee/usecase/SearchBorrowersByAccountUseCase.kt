package payee.usecase

import kotlinx.coroutines.flow.Flow
import payee.model.Payee
import payee.model.PayeeType
import session.repository.SessionRepository
import transaction.model.Borrower
import transaction.repository.PayeeRepository
import javax.inject.Inject

class SearchPayeesByTypeUseCase @Inject constructor(
    private val repository: PayeeRepository,
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(searchQuery: String, payeeType: PayeeType): Flow<List<Payee>> {
        val userId = sessionRepository.getCurrentUserId()
        return repository.searchPayeesByType(userId, searchQuery, payeeType)
    }
}