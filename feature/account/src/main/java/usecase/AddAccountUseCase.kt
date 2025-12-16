package usecase

import account.model.Account
import account.model.AccountType
import account.repository.AccountRepository
import session.repository.SessionRepository
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(
    private val repository: AccountRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        username: String,
        type: AccountType,
        balance: Double
    ): Long {
        val userId = sessionRepository.getCurrentUserId() ?: 1L

        val account = Account(
            userId = userId,
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