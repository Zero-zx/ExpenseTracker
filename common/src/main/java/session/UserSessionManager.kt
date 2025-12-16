package session

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages user session using SharedPreferences
 * Stores current userId and selected accountId
 */
@Singleton
class UserSessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentAccountIdFlow = MutableStateFlow<Long?>(getCurrentAccountId())
    val currentAccountIdFlow: Flow<Long?> = _currentAccountIdFlow.asStateFlow()

    companion object {
        private const val PREFS_NAME = "user_session_prefs"
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_ACCOUNT_ID = "current_account_id"
        private const val DEFAULT_USER_ID = 1L
    }

    /**
     * Get current user ID, defaults to 1 for now
     */
    fun getCurrentUserId(): Long {
        return prefs.getLong(KEY_USER_ID, DEFAULT_USER_ID)
    }

    /**
     * Set current user ID
     */
    fun setCurrentUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    /**
     * Get currently selected account ID
     */
    fun getCurrentAccountId(): Long? {
        val accountId = prefs.getLong(KEY_ACCOUNT_ID, -1L)
        return if (accountId == -1L) null else accountId
    }

    /**
     * Set currently selected account ID
     */
    fun setCurrentAccountId(accountId: Long) {
        prefs.edit().putLong(KEY_ACCOUNT_ID, accountId).apply()
        _currentAccountIdFlow.value = accountId
    }

    /**
     * Clear all session data (logout)
     */
    fun clearSession() {
        prefs.edit().clear().apply()
        _currentAccountIdFlow.value = null
    }

    /**
     * Check if user has selected an account
     */
    fun hasSelectedAccount(): Boolean {
        return getCurrentAccountId() != null
    }
}

