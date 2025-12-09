package account.usecase

import account.model.Account
import account.repository.AccountRepository
import javax.inject.Inject

class GetAccountByIdUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(accountId: Long): Account? {
        return accountRepository.getAccountById(accountId)
    }
}