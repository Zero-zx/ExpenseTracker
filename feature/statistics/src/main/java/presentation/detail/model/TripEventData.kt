package presentation.detail.model

import transaction.model.Event

data class TripEventData(
    val event: Event,
    val totalAmount: Double // Total expense for this event
)


