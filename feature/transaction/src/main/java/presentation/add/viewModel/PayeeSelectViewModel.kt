package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import contact.usecase.GetAllPhoneContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.PayeeTransaction
import transaction.usecase.GetPayeesByAccountUseCase
import transaction.usecase.GetRecentPayeesByAccountUseCase
import javax.inject.Inject

@HiltViewModel
class PayeeSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getPayeesByAccountUseCase: GetPayeesByAccountUseCase,
    private val getRecentPayeesByAccountUseCase: GetRecentPayeesByAccountUseCase,
    private val getAllPhoneContactsUseCase: GetAllPhoneContactsUseCase
) : BaseViewModel<List<PayeeTransaction>>() {

    companion object {
        private const val ACCOUNT_ID = 1L // TODO: Get from account repository
    }

    init {
        loadPayees()
    }

    fun loadPayees() {
        viewModelScope.launch {
            getPayeesByAccountUseCase(ACCOUNT_ID)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { payees ->
                    setSuccess(payees)
                }
        }
    }

    fun loadRecentPayees() {
        viewModelScope.launch {
            getRecentPayeesByAccountUseCase(ACCOUNT_ID)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { payees ->
                    setSuccess(payees)
                }
        }
    }

    fun getAllPhoneContacts() {
        viewModelScope.launch {
            try {
                setLoading()
                val contacts = getAllPhoneContactsUseCase()
                // Contacts are not temporary, they're from device
                setSuccess(contacts.map { contact ->
                    PayeeTransaction(
                        id = contact.id,
                        name = contact.displayName,
                        ACCOUNT_ID
                    )
                })
            } catch (e: Exception) {
                setError(e.message ?: "Failed to load contacts")
            }
        }
    }

}
