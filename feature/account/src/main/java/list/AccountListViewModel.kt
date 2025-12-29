package list

import account.model.Account
import account.usecase.DeleteAccountUseCase
import account.usecase.GetTotalAmountUseCase
import account.usecase.GetUserAccountsUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val getUserAccountsUseCase: GetUserAccountsUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getTotalAmountUseCase: GetTotalAmountUseCase,
    private val navigator: Navigator
) : BaseViewModel<List<Account>>() {

    val totalAmount: StateFlow<Double> = getTotalAmountUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getUserAccountsUseCase()
                .onStart {
                    setLoading()
                }
                .catch { exception ->
                    setError(exception.message.toString())
                }
                .collect { accounts ->
                    setSuccess(accounts)
                }
        }
    }

    fun deleteAccount(account: Account) {
        viewModelScope.launch {
            deleteAccountUseCase(account)
        }
    }

    fun navigateToAddAccount() {
        navigator.navigateToAddAccount()
    }

    fun refresh() {
        resetState()
        loadAccounts()
    }
}

