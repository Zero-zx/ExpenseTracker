package com.example.home.event.add

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.usecase.AddEventUseCase
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val addEventUseCase: AddEventUseCase,
    private val navigator: Navigator
) : BaseViewModel<Long>() {

    private val _participants = MutableStateFlow<List<String>>(emptyList())
    val participants: StateFlow<List<String>> = _participants.asStateFlow()

    fun addEvent(
        eventName: String,
        startDate: Long,
        endDate: Long?,
        numberOfParticipants: Int,
        accountId: Long
    ) {
        viewModelScope.launch {
            setLoading()
            try {
                val event = addEventUseCase(
                    eventName = eventName,
                    startDate = startDate,
                    endDate = endDate,
                    numberOfParticipants = numberOfParticipants,
                    accountId = accountId,
                    participants = _participants.value
                )
                setSuccess(event.id)
            } catch (e: Exception) {
                setError(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun updateParticipants(participants: List<String>) {
        _participants.value = participants
    }

    fun navigateToAddParticipants() {
        navigator.navigateToAddParticipants()
    }

    fun navigateBack() {
        navigator.popBackStack()
    }
}

