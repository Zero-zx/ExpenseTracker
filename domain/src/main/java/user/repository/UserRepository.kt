package user.repository

import kotlinx.coroutines.flow.Flow
import user.model.User

/**
 * Repository interface for User operations
 */
interface UserRepository {
    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: Long): User?

    /**
     * Get all users
     */
    fun getAllUsers(): Flow<List<User>>

    /**
     * Insert or update a user
     */
    suspend fun insertUser(user: User): Long

    /**
     * Get user by Firebase UID (for future Firebase integration)
     */
    suspend fun getUserByFirebaseUid(firebaseUid: String): User?

    /**
     * Initialize default user if not exists
     */
    suspend fun initializeDefaultUser(user: User)
}

