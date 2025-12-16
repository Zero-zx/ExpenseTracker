package transaction.model

data class Event(
    val id: Long = 0,
    val eventName: String,
    val startDate: Long,
    val endDate: Long?,
    val numberOfParticipants: Int,
    val accountId: Long,
    val isActive: Boolean = true,
    val participants: List<String> = emptyList()
)
