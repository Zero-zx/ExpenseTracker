package domain.usecase

import data.model.Account
import domain.repository.AccountRepository
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(
        username: String,
        type: data.model.AccountType,
        balance: Double
    ): Long {
        val account = Account(
            username = username,
            type = type,
            balance = balance,
            createAt = System.currentTimeMillis()
        )
        require(account.username.isNotBlank()) { "Username cannot be blank" }
        require(account.balance >= 0) { "Balance must be greater than or equal to 0" }

        return repository.insertAccount(account)
    }
}

