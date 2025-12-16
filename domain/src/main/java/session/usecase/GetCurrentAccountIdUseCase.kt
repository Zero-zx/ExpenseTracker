package session.usecase

import session.repository.SessionRepository
import javax.inject.Inject

/**
 * Use case to get the currently selected account ID from session
 */
class GetCurrentAccountIdUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Long? {
        return sessionRepository.getCurrentAccountId()
    }
}

