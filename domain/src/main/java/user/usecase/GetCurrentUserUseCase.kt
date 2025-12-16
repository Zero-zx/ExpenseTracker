package user.usecase

import session.repository.SessionRepository
import user.model.User
import user.repository.UserRepository
import javax.inject.Inject

/**
 * Use case to get the current logged-in user
 */
class GetCurrentUserUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? {
        val userId = sessionRepository.getCurrentUserId()
        return if (userId != null) {
            userRepository.getUserById(userId)
        } else {
            null
        }
    }
}

