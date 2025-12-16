package session.usecase

import account.repository.AccountRepository
import kotlinx.coroutines.flow.firstOrNull
import session.repository.SessionRepository
import javax.inject.Inject

/**
 * Use case to initialize session with first available account
 * Should be called after app initialization
 */
class InitializeSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke() {
        // If no account is selected, select the first one
        if (sessionRepository.getCurrentAccountId() == null) {
            val userId = sessionRepository.getCurrentUserId() ?: 1L
            val accountList = accountRepository.getUserAccounts(userId).firstOrNull()

            if (!accountList.isNullOrEmpty()) {
                sessionRepository.setCurrentAccountId(accountList.first().id)
            }
        }
    }
}

