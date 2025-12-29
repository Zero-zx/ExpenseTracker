package session.usecase

import session.repository.SessionRepository
import javax.inject.Inject

/**
 * Use case to select/switch to a different account
 */
class SelectAccountUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(accountId: Long) {
        sessionRepository.setCurrentAccountId(accountId)
    }
}

