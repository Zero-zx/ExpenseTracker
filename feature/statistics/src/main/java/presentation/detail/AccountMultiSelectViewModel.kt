package presentation.detail

import account.model.Account
import account.usecase.GetUserAccountsUseCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountMultiSelectViewModel @Inject constructor(
    private val getUserAccountsUseCase: GetUserAccountsUseCase
) : BaseViewModel<List<Account>>() {

    private val _allAccountIds = MutableLiveData<List<Long>>()
    val allAccountIds: LiveData<List<Long>> = _allAccountIds

    fun loadAccounts() {
        viewModelScope.launch {
            getUserAccountsUseCase()
                .onStart { setLoading() }
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { accounts ->
                    setSuccess(accounts)
                    _allAccountIds.value = accounts.map { it.id }
                }
        }
    }

    fun selectAllAccounts() {
        viewModelScope.launch {
            getUserAccountsUseCase()
                .collect { accounts ->
                    _allAccountIds.value = accounts.map { it.id }
                }
        }
    }
}

