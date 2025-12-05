package list

import account.model.Account
import account.usecase.GetAccountsUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val navigator: Navigator
) : BaseViewModel<List<Account>>() {


    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAccountsUseCase()
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


    fun refresh() {
        resetState()
        loadAccounts()
    }
}

