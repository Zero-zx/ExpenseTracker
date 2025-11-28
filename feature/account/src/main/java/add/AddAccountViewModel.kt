package add

import AddAccountUiState
import account.model.AccountType
import account.usecase.AddAccountUseCase
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ActivityScenario.launch
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val addAccountUseCase: AddAccountUseCase
) : BaseViewModel<Long>() {

    private val _selectedAccountType = MutableStateFlow<AccountType?>(null)
    val selectedAccountType = _selectedAccountType.asStateFlow()

    fun updateAccountType(accountType: AccountType) {
        _selectedAccountType.value = accountType
    }

    fun addAccount(
        username: String,
        type: AccountType,
        balance: Double
    ) {
        viewModelScope.launch {
            setLoading()
            try {
                val accountId = addAccountUseCase(
                    username = username,
                    type = type,
                    balance = balance
                )
                setSuccess(accountId)
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error occurred")
            }
        }
    }

}

