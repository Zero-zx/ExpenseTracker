package presentation

import data.model.Account

/**
 * UIState for account list operations.
 * Uses sealed class pattern for type-safe state management.
 */
sealed class AccountListUiState {
    object Loading : AccountListUiState()
    object Empty : AccountListUiState()
    data class Success(val accounts: List<Account>) : AccountListUiState()
    data class Error(val message: String) : AccountListUiState()
}

/**
 * UIState for add account operations.
 * Uses sealed class pattern for type-safe state management.
 */
sealed class AddAccountUiState {
    object Initial : AddAccountUiState()
    object Loading : AddAccountUiState()
    data class Success(val accountId: Long) : AddAccountUiState()
    data class Error(val message: String) : AddAccountUiState()
}

