package transaction.model

data class EventWithParticipants(
    val event: Event,
    val participants: List<EventParticipant>
)

