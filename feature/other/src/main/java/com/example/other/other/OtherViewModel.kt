package com.example.other.other

import androidx.lifecycle.viewModelScope
import base.BaseViewModel
import com.example.other.other.model.FeatureItem
import com.example.other.other.model.SettingItem
import com.example.other.other.model.UtilityItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import navigation.Navigator
import javax.inject.Inject

@HiltViewModel
class OtherViewModel @Inject constructor(
    private val navigator: Navigator
) : BaseViewModel<OtherData>() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _syncStatus = MutableStateFlow<SyncStatus?>(null)
    val syncStatus: StateFlow<SyncStatus?> = _syncStatus.asStateFlow()

    init {
        loadUserProfile()
        loadSyncStatus()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // TODO: Load from repository/use case
        }
    }

    private fun loadSyncStatus() {
        viewModelScope.launch {
            // TODO: Load from repository/use case
            val lastSyncTime = System.currentTimeMillis()
            _syncStatus.value = SyncStatus(
                lastSyncTime = lastSyncTime,
                isSyncing = false
            )
        }
    }

    fun sync() {
        viewModelScope.launch {
            _syncStatus.value = _syncStatus.value?.copy(isSyncing = true)
            // TODO: Implement actual sync logic
            kotlinx.coroutines.delay(2000) // Simulate sync
            _syncStatus.value = _syncStatus.value?.copy(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis()
            )
        }
    }

    fun navigateToPremium() {
        // TODO: Navigate to premium screen
    }

    fun navigateToFeature(feature: FeatureItem) {
        // TODO: Navigate based on feature type
    }

    fun navigateToUtility(utility: UtilityItem) {
        // TODO: Navigate based on utility type
    }

    fun navigateToSetting(setting: SettingItem) {
        // TODO: Navigate based on setting type
    }
}

data class OtherData(
    val userProfile: UserProfile?,
    val syncStatus: SyncStatus?
)

data class UserProfile(
    val username: String,
    val email: String,
    val coins: Int,
    val referralCode: String
)

data class SyncStatus(
    val lastSyncTime: Long,
    val isSyncing: Boolean
)

