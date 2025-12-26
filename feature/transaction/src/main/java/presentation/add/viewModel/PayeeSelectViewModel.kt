package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import contact.usecase.GetAllPhoneContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import payee.model.Payee
import payee.model.PayeeType
import payee.usecase.DeletePayeeUseCase
import payee.usecase.GetRecentPayeesByTypeUseCase
import payee.usecase.SearchPayeesByTypeUseCase
import payee.usecase.UpdatePayeeUseCase
import javax.inject.Inject

@HiltViewModel
class PayeeSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getRecentPayeesByAccountUseCase: GetRecentPayeesByTypeUseCase,
    private val getAllPhoneContactsUseCase: GetAllPhoneContactsUseCase,
    private val updatePayeeUseCase: UpdatePayeeUseCase,
    private val deletePayeeUseCase: DeletePayeeUseCase,
    private val searchPayeesByTypeUseCase: SearchPayeesByTypeUseCase
) : BaseViewModel<List<Payee>>() {

    private val _payeeType = MutableStateFlow(PayeeType.PAYEE)
    val payeeType: StateFlow<PayeeType> = _payeeType

    private val _isContact = MutableStateFlow(false)
    val isContact: StateFlow<Boolean> = _isContact

    fun updatePayeeType(type: PayeeType) {
        _payeeType.value = type
    }

    fun loadRecentPayees() {
        _isContact.value = false
        viewModelScope.launch {
            getRecentPayeesByAccountUseCase(_payeeType.value)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { payees ->
                    setSuccess(payees)
                }
        }
    }

    fun getAllPhoneContacts() {
        _isContact.value = true
        viewModelScope.launch {
            try {
                setLoading()
                val contacts = getAllPhoneContactsUseCase()
                // Contacts are not temporary, they're from device
                setSuccess(contacts.map { contact ->
                    Payee(
                        id = contact.id,
                        name = contact.displayName,
                        isFromContacts = true,
                        payeeType = _payeeType.value
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

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                if (_isContact.value) getAllPhoneContacts()
                else {
                    loadRecentPayees()
                }
            } else {
                // Search in database
                searchPayeesByTypeUseCase(query, _payeeType.value).catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }.collect { payees ->
                    setSuccess(payees)
                }
            }
        }
    }

}
