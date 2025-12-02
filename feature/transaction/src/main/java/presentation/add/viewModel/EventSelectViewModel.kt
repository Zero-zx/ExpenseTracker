package presentation.add.viewModel

import account.model.Account
import account.usecase.GetAccountsUseCase
import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import transaction.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.Event
import transaction.usecase.GetEventsByAccountUseCase
import javax.inject.Inject

@HiltViewModel
class EventSelectViewModel @Inject constructor(
    private val getEventsByAccountUseCase: GetEventsByAccountUseCase,
    private val navigator: Navigator
) : BaseViewModel<List<Event>>() {

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getEventsByAccountUseCase(1)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { events ->
                    if (events.isNotEmpty()) {
                        setSuccess(events)
                    }
                }
        }
    }

    fun refresh() {
        setLoading()
        loadAccounts()
    }
}

