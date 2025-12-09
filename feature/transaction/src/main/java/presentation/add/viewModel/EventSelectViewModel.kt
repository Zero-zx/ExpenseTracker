package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.Event
import transaction.usecase.AddEventUseCase
import transaction.usecase.GetEventsByAccountUseCase
import javax.inject.Inject

@HiltViewModel
class EventSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getEventsByAccountUseCase: GetEventsByAccountUseCase,
    private val addEventUseCase: AddEventUseCase
) : BaseViewModel<List<Event>>() {

    init {
        loadEvents()
    }

    private fun loadEvents() {
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

    fun addEvent(eventName: String) {
        if (eventName.isBlank()) {
            setError("Event name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                setLoading()
                // Use current timestamp as start date, no end date, and default participant (user)
                val currentTime = System.currentTimeMillis()
                addEventUseCase(
                    eventName = eventName,
                    startDate = currentTime,
                    endDate = null,
                    numberOfParticipants = 1,
                    accountId = 1,
                    participants = listOf("Me")
                )
                loadEvents() // Reload events after adding
            } catch (e: Exception) {
                setError(e.message ?: "Failed to add event")
            }
        }
    }
}

