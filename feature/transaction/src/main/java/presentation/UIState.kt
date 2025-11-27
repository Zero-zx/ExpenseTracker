package presentation

import data.model.Category
import data.model.Transaction

/**
 * UIState for category loading operations.
 * Uses sealed class pattern for type-safe state management.
 */
sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Success(val categories: List<Category>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

/**
 * UIState for add transaction operations.
 * Uses sealed class pattern for type-safe state management.
 */
sealed class AddTransactionUiState {
    abstract val selectedCategory: Category?
    
    data class Initial(override val selectedCategory: Category? = null) : AddTransactionUiState()
    data class Loading(override val selectedCategory: Category? = null) : AddTransactionUiState()
    data class Success(
        val transactionId: Long,
        override val selectedCategory: Category? = null
    ) : AddTransactionUiState()
    data class Error(
        val message: String,
        override val selectedCategory: Category? = null
    ) : AddTransactionUiState()
    
    /**
     * Helper function to update selectedCategory while preserving the current state type.
     */
    fun withSelectedCategory(category: Category?): AddTransactionUiState = when (this) {
        is Initial -> Initial(category)
        is Loading -> Loading(category)
        is Success -> Success(transactionId, category)
        is Error -> Error(message, category)
    }
}

/**
 * UIState for transaction list operations.
 * Uses sealed class pattern for type-safe state management.
 */
sealed class TransactionListUiState {
    object Loading : TransactionListUiState()
    object Empty : TransactionListUiState()
    data class Success(val transactions: List<Transaction>) : TransactionListUiState()
    data class Error(val message: String) : TransactionListUiState()
}