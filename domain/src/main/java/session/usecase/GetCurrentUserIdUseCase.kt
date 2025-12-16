package session.usecase

import session.repository.SessionRepository
import javax.inject.Inject

/**
 * Use case to get the current user ID from session
 */
class GetCurrentUserIdUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Long? {
        return sessionRepository.getCurrentUserId()
    }
}

