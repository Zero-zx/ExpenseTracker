package user.usecase

import user.model.User
import user.repository.UserRepository
import javax.inject.Inject

/**
 * Use case to initialize the default user (userId = 1)
 * This will be used until Firebase authentication is implemented
 */
class InitializeDefaultUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        val defaultUser = User(
            id = 1L,
            name = "Default User",
            email = null,
            firebaseUid = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userRepository.initializeDefaultUser(defaultUser)
    }
}

