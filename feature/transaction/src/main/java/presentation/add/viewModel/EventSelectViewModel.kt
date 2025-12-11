package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.Event
import transaction.usecase.GetEventsByAccountUseCase
import javax.inject.Inject

@HiltViewModel
class EventSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getEventsByAccountUseCase: GetEventsByAccountUseCase
) : BaseViewModel<List<Event>>() {

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            getEventsByAccountUseCase(1)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { events ->
                    setSuccess(events)
                }
        }
    }
}

