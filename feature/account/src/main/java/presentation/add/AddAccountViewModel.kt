package presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.AccountType
import domain.usecase.AddAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import presentation.AddAccountUiState
import javax.inject.Inject

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val addAccountUseCase: AddAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddAccountUiState>(AddAccountUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun addAccount(
        username: String,
        type: AccountType,
        balance: Double
    ) {
        viewModelScope.launch {
            _uiState.value = AddAccountUiState.Loading
            try {
                val accountId = addAccountUseCase(
                    username = username,
                    type = type,
                    balance = balance
                )
                _uiState.value = AddAccountUiState.Success(accountId)
            } catch (e: Exception) {
                _uiState.value = AddAccountUiState.Error(
                    message = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AddAccountUiState.Initial
    }
}

