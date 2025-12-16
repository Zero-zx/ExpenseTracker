package user.model

/**
 *
 * Firebase UID will be added later for authentication.
 * This is separate from Account - one User can have multiple Accounts.
 * User domain model representing a user in the application.
 */
data class User(
    val id: Long = 0,
    val firebaseUid: String? = null,
    val name: String,
    val email: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
