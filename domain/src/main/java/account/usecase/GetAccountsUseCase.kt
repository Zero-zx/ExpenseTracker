package account.usecase

import account.repository.AccountRepository
import account.model.Account
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> {
        return accountRepository.getAllAccounts()
    }
}

