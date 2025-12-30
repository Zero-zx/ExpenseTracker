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
import transaction.model.Location
import transaction.usecase.DeleteLocationUseCase
import transaction.usecase.GetLocationsByAccountUseCase
import transaction.usecase.SearchLocationsByAccountUseCase
import transaction.usecase.UpdateLocationUseCase
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class LocationSelectViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getLocationsByAccountUseCase: GetLocationsByAccountUseCase,
    private val searchLocationsByAccountUseCase: SearchLocationsByAccountUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : BaseViewModel<List<Location>>() {

    private val _searchQuery = MutableStateFlow("")
    private val accountId = 1L // TODO: Get from session

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            getLocationsByAccountUseCase(accountId)
                .catch { exception ->
                    setError(exception.message ?: "Unknown error occurred")
                }
                .collect { locations ->
                    if(locations.isNotEmpty()) setSuccess(locations)
                    else resetState()
                }
        }
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                // Empty query - get all locations
                getLocationsByAccountUseCase(accountId)
            } else {
                // Search in database
                searchLocationsByAccountUseCase(accountId, query)
            }.catch { exception ->
                setError(exception.message ?: "Unknown error occurred")
            }.collect { locations ->
                setSuccess(locations)
            }

        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun updateLocation(location: Location) {
        viewModelScope.launch {
            try {
                updateLocationUseCase(location)
                // Reload locations after update
                loadLocations()
            } catch (e: Exception) {
                setError(e.message ?: "Failed to update location")
            }
        }
    }

    fun deleteLocation(locationId: Long) {
        viewModelScope.launch {
            try {
                deleteLocationUseCase(locationId)
                // Reload locations after delete
                loadLocations()
            } catch (e: Exception) {
                setError(e.message ?: "Failed to delete location")
            }
        }
    }
}



