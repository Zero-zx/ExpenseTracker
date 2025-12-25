package account.usecase

import account.model.Account
import account.model.AccountType
import account.repository.AccountRepository
import session.repository.SessionRepository
import user.model.User
import user.repository.UserRepository
import javax.inject.Inject

/**
 * Use case to initialize default user and admin account
 * Creates user with ID=1 and links the admin account to it
 */
class InitializeAdminUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        // First, create default user if not exists
        val defaultUser = User(
            id = 1L,
            name = "Default User",
            email = null,
            firebaseUid = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userRepository.initializeDefaultUser(defaultUser)

        // Then create admin account linked to that user
        val account = Account(
            username = "Hieu",
            type = AccountType.CASH,
            balance = 10000.0,
            createAt = System.currentTimeMillis()
        )
        accountRepository.initializeAdmin(account)

        // Set default session
        sessionRepository.setCurrentUserId(1L)
    }
}