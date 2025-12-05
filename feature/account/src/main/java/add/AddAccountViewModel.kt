package add

import account.model.AccountType
import usecase.AddAccountUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val addAccountUseCase: AddAccountUseCase,
    private val navigator: Navigator
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

    fun navigateBack() {
        navigator.popBackStack()
    }
}

