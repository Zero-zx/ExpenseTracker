package account.usecase

import account.model.Account
import account.repository.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account) {
        accountRepository.deleteAccount(account)
    }
}