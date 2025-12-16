package repository

import dao.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mapper.toDomain
import mapper.toEntity
import user.model.User
import user.repository.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)?.toDomain()
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertUser(user: User): Long {
        return userDao.insert(user.toEntity())
    }

    override suspend fun getUserByFirebaseUid(firebaseUid: String): User? {
        return userDao.getUserByFirebaseUid(firebaseUid)?.toDomain()
    }

    override suspend fun initializeDefaultUser(user: User) {
        val existingUser = userDao.getUserById(user.id)
        if (existingUser == null) {
            userDao.insert(user.toEntity())
        }
    }
}

