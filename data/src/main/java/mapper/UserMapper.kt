package mapper

import model.UserEntity
import user.model.User

internal fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        firebaseUid = firebaseUid,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

internal fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        firebaseUid = firebaseUid,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

