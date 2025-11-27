package presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import domain.usecase.GetTransactionsUseCase
import presentation.TransactionListUiState
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionListUiState>(TransactionListUiState.Loading)
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    // For demo purposes - in real app, get from auth/session
    private val currentAccountId = 1L

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase(currentAccountId)
                .catch { exception ->
                    _uiState.value = TransactionListUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { transactions ->
                    _uiState.value = if (transactions.isEmpty()) {
                        TransactionListUiState.Empty
                    } else {
                        TransactionListUiState.Success(transactions)
                    }
                }
        }
    }

    fun refresh() {
        _uiState.value = TransactionListUiState.Loading
        loadTransactions()
    }
}
