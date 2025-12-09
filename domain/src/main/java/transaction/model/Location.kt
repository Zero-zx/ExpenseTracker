package transaction.model

/**
 * Location model for transactions
 * Represents where a transaction occurred
 */
data class Location(
    val id: Long = 0,
    val name: String,
    val accountId: Long, // Account that owns this location
    val latitude: Double? = null, // Optional GPS coordinates
    val longitude: Double? = null
)

