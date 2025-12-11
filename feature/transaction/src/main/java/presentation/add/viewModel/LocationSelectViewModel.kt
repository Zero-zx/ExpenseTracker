package presentation.add.viewModel

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import navigation.Navigator
import transaction.model.Location
import transaction.usecase.GetLocationsByAccountUseCase
import transaction.usecase.SearchLocationsByAccountUseCase
import javax.inject.Inject

@HiltViewModel
class LocationSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getLocationsByAccountUseCase: GetLocationsByAccountUseCase,
    private val searchLocationsByAccountUseCase: SearchLocationsByAccountUseCase
) : BaseViewModel<List<Location>>() {

    companion object {
        private const val ACCOUNT_ID = 1L
    }

    init {
        loadLocations()
    }

    fun loadLocations() {
        viewModelScope.launch {
            getLocationsByAccountUseCase(ACCOUNT_ID)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .onEach { locations ->
                    setSuccess(locations)
                }
                .launchIn(viewModelScope)
        }
    }

    fun searchLocations(query: String) {
        if (query.isBlank()) {
            loadLocations()
            return
        }

        viewModelScope.launch {
            searchLocationsByAccountUseCase(ACCOUNT_ID, query)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .onEach { locations ->
                    setSuccess(locations)
                }
                .launchIn(viewModelScope)
        }
    }
}

