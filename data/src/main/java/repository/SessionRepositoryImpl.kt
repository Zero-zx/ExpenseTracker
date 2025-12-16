package repository

import kotlinx.coroutines.flow.Flow
import session.UserSessionManager
import session.repository.SessionRepository
import javax.inject.Inject

internal class SessionRepositoryImpl @Inject constructor(
    private val sessionManager: UserSessionManager
) : SessionRepository {

    override fun getCurrentUserId(): Long? {
        return sessionManager.getCurrentUserId()
    }

    override suspend fun setCurrentUserId(userId: Long) {
        sessionManager.setCurrentUserId(userId)
    }

    override fun getCurrentAccountId(): Long? {
        return sessionManager.getCurrentAccountId()
    }

    override suspend fun setCurrentAccountId(accountId: Long) {
        sessionManager.setCurrentAccountId(accountId)
    }

    override fun observeCurrentAccountId(): Flow<Long?> {
        return sessionManager.currentAccountIdFlow
    }

    override suspend fun clearSession() {
        sessionManager.clearSession()
    }
}

