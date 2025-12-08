package presentation.add.viewModel

import account.model.Account
import account.usecase.GetAccountsUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class AccountSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getAccountsUseCase: GetAccountsUseCase
) : BaseViewModel<List<Account>>() {

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAccountsUseCase()
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { accounts ->
                    if (accounts.isNotEmpty()) {
                        setSuccess(accounts)
                    }
                }
        }
    }

    fun refresh() {
        setLoading()
        loadAccounts()
    }

    fun navigateBack() {
        navigator.popBackStack()
    }
}

