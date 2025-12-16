package account.usecase

import account.model.Account
import account.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import session.repository.SessionRepository
import javax.inject.Inject

/**
 * Use case to get accounts for the current logged-in user
 */
class GetUserAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<List<Account>> {
        val userId = sessionRepository.getCurrentUserId() ?: 1L
        return accountRepository.getUserAccounts(userId)
    }
}
