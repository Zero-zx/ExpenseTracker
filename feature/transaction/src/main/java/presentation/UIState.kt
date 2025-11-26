package presentation

import data.model.Category

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<Category>? = null,
    val error: String? = null
)

data class AddTransactionUiState(
    val isLoading: Boolean = false,
    val transactionId: Long? = null,
    val error: String? = null,
    val selectedCategory: Category? = null
)