package session.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun clearSession()


    fun observeCurrentAccountId(): Flow<Long?>


    suspend fun setCurrentAccountId(accountId: Long)


    fun getCurrentAccountId(): Long?


    suspend fun setCurrentUserId(userId: Long)

    fun getCurrentUserId(): Long = 1

}