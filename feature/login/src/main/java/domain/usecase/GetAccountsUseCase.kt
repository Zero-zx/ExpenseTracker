package domain.usecase

import data.model.Account
import domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> {
        return accountRepository.getAllAccounts()
    }
}

