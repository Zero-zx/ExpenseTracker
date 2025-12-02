package com.example.home.event.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.usecase.GetEventsByAccountUseCase
import javax.inject.Inject

@HiltViewModel
class EventListViewModel @Inject constructor(
    private val getEventsByAccountUseCase: GetEventsByAccountUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventListUiState>(EventListUiState.Loading)
    val uiState: StateFlow<EventListUiState> = _uiState.asStateFlow()

    fun loadEvents(accountId: Long) {
        viewModelScope.launch {
            _uiState.value = EventListUiState.Loading
            getEventsByAccountUseCase(accountId)
                .catch { e ->
                    _uiState.value = EventListUiState.Error(
                        e.message ?: "Unknown error occurred"
                    )
                }
                .collect { events ->
                    _uiState.value = if (events.isEmpty()) {
                        EventListUiState.Empty
                    } else {
                        EventListUiState.Success(events)
                    }
                }
        }
    }

    fun navigateToAddEvent() {
        navigator.navigateToAddEvent()
    }

    fun navigateBack() {
        navigator.popBackStack()
    }
}

