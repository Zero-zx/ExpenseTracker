package presentation.add

import android.accounts.Account
import android.app.usage.UsageEvents
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Category
import data.model.Transaction
import domain.usecase.AddTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun addTransaction(
        amount: Double,
        description: String?,
        category: Category,
        account: Account,
        event: UsageEvents.Event? = null,
        partner: Nothing? = null
    ) {
        viewModelScope.launch {
            _uiState.value = AddTransactionUiState.Loading

            try {

                val id = addTransactionUseCase(
                    amount = amount,
                    category = category,
                    description = description
                )
                _uiState.value = AddTransactionUiState.Success(id)
            } catch (e: Exception) {
                _uiState.value = AddTransactionUiState.Error(
                    e.message ?: "Failed to add transaction"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AddTransactionUiState.Idle
    }
}

sealed class AddTransactionUiState {
    object Idle : AddTransactionUiState()
    object Loading : AddTransactionUiState()
    data class Success(val transactionId: Long) : AddTransactionUiState()
    data class Error(val message: String) : AddTransactionUiState()
}