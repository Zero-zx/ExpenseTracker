package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.Event
import transaction.usecase.GetEventsByAccountUseCase
import transaction.usecase.SearchEventsByAccountUseCase
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class EventSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getEventsByAccountUseCase: GetEventsByAccountUseCase,
    private val searchEventsByAccountUseCase: SearchEventsByAccountUseCase
) : BaseViewModel<List<Event>>() {

    private val _searchQuery = MutableStateFlow("")
    private val accountId = 1L // TODO: Get from session

    init {
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            getEventsByAccountUseCase(accountId)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { events ->
                    if(events.isNotEmpty()) setSuccess(events)
                    else resetState()
                }
        }
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                // Empty query - get all events
                getEventsByAccountUseCase(accountId)
            } else {
                // Search in database
                searchEventsByAccountUseCase(accountId, query)
            }.catch { exception ->
                setError(exception.message ?: "Unknown error occurred")
            }.collect { events ->
                setSuccess(events)
            }

        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}

