package domain.usecase

import data.model.Account
import data.model.AccountType
import domain.repository.AccountRepository
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