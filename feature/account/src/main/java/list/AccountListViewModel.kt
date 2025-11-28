package list

import AccountListUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import account.usecase.GetAccountsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountListUiState>(AccountListUiState.Loading)
    val uiState: StateFlow<AccountListUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAccountsUseCase()
                .catch { exception ->
                    _uiState.value = AccountListUiState.Error(
                        exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { accounts ->
                    _uiState.value = if (accounts.isEmpty()) {
                        AccountListUiState.Empty
                    } else {
                        AccountListUiState.Success(accounts)
                    }
                }
        }
    }

    fun goToAddAccount() {
        navigator.navigateToAddAccount()
    }


    fun refresh() {
        _uiState.value = AccountListUiState.Loading
        loadAccounts()
    }
}

