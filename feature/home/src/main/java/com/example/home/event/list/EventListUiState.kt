package com.example.home.event.list

import transaction.model.Event

sealed class EventListUiState {
    object Loading : EventListUiState()
    object Empty : EventListUiState()
    data class Success(val events: List<Event>) : EventListUiState()
    data class Error(val message: String) : EventListUiState()
}

