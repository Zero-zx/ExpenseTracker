package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import contact.usecase.GetAllPhoneContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.Payee
import transaction.usecase.DeletePayeeUseCase
import transaction.usecase.GetPayeesByAccountUseCase
import transaction.usecase.GetRecentPayeesByAccountUseCase
import transaction.usecase.UpdatePayeeUseCase
import javax.inject.Inject

@HiltViewModel
class PayeeSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getPayeesByAccountUseCase: GetPayeesByAccountUseCase,
    private val getRecentPayeesByAccountUseCase: GetRecentPayeesByAccountUseCase,
    private val getAllPhoneContactsUseCase: GetAllPhoneContactsUseCase,
    private val updatePayeeUseCase: UpdatePayeeUseCase,
    private val deletePayeeUseCase: DeletePayeeUseCase
) : BaseViewModel<List<Payee>>() {

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
                    Payee(
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

    fun updatePayee(payee: Payee) {
        viewModelScope.launch {
            try {
                updatePayeeUseCase(payee)
                // Reload payees after update
                loadRecentPayees()
            } catch (e: Exception) {
                setError(e.message ?: "Failed to update payee")
            }
        }
    }

    fun deletePayee(payeeId: Long) {
        viewModelScope.launch {
            try {
                deletePayeeUseCase(payeeId)
                // Reload payees after delete
                loadRecentPayees()
            } catch (e: Exception) {
                setError(e.message ?: "Failed to delete payee")
            }
        }
    }

}
