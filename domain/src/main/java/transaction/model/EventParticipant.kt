package transaction.model

data class EventParticipant(
    val id: Long = 0,
    val eventId: Long,
    val participantName: String
)

