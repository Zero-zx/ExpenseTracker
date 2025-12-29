package payee.usecase

import kotlinx.coroutines.flow.Flow
import payee.model.Payee
import payee.model.PayeeType
import session.repository.SessionRepository
import transaction.repository.PayeeRepository
import javax.inject.Inject

class GetPayeesByAccountUseCase @Inject constructor(
    private val repository: PayeeRepository,
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(payeeType: PayeeType): Flow<List<Payee>> {
        val userId = sessionRepository.getCurrentUserId()
        return repository.getAllPayeesByType(userId, payeeType)
    }
}