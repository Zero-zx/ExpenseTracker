package account.usecase

import account.repository.AccountRepository
import account.model.Account
import account.model.AccountType
import javax.inject.Inject

class InitializeAdminUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke() {
        val account = Account(
            username = "Hieu",
            type = AccountType.PREMIUM,
            balance = 10000.0,
            createAt = System.currentTimeMillis()
        )
        accountRepository.initializeAdmin(account)

    }
}