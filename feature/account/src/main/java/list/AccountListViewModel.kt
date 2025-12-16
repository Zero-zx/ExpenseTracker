package list

import account.model.Account
import account.usecase.GetUserAccountsUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import navigation.Navigator
import session.usecase.GetCurrentAccountIdUseCase
import session.usecase.SelectAccountUseCase
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val getUserAccountsUseCase: GetUserAccountsUseCase,
    private val selectAccountUseCase: SelectAccountUseCase,
    private val getCurrentAccountIdUseCase: GetCurrentAccountIdUseCase,
    private val navigator: Navigator
) : BaseViewModel<List<Account>>() {

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

    fun goToAddAccount() {
        navigator.navigateToAddAccount()
    }

    fun selectAccount(accountId: Long) {
        viewModelScope.launch {
            selectAccountUseCase(accountId)
        }
    }

    fun getCurrentAccountId(): Long? {
        return getCurrentAccountIdUseCase()
    }

    fun refresh() {
        resetState()
        loadAccounts()
    }
}

