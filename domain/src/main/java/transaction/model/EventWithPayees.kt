package transaction.model

data class EventWithPayees(
    val event: Event,
    val participants: List<Payee>
)

