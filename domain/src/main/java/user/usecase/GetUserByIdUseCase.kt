package user.usecase

import user.model.User
import user.repository.UserRepository
import javax.inject.Inject

/**
 * Use case to get a user by their ID
 */
class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): User? {
        return userRepository.getUserById(userId)
    }
}

