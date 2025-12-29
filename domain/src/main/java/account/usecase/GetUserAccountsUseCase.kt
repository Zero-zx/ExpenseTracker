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
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> {
        return accountRepository.getAccounts()
    }
}
