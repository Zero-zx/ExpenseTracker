package presentation.list

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import transaction.model.Transaction
import transaction.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : BaseViewModel<List<Transaction>>() {
    private val currentAccountId = 1L

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase(currentAccountId)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { transactions ->
                    if (transactions.isNotEmpty()) {
                        setSuccess(transactions)
                    }
                }
        }
    }

    fun refresh() {
        setLoading()
        loadTransactions()
    }
}
